package com.example.tacoshop.service.impl;

import com.example.tacoshop.config.ApplicationConstants;
import com.example.tacoshop.dto.mapper.UserMapper;
import com.example.tacoshop.dto.request.CreateOrderRequest;
import com.example.tacoshop.dto.request.OrderItemRequest;
import com.example.tacoshop.dto.request.ProductRequest;
import com.example.tacoshop.dto.response.OrderItemResponse;
import com.example.tacoshop.dto.response.OrderResponse;
import com.example.tacoshop.dto.response.PageResponse;
import com.example.tacoshop.entity.OrderEntity;
import com.example.tacoshop.entity.OrderItem;
import com.example.tacoshop.entity.Product;
import com.example.tacoshop.entity.User;
import com.example.tacoshop.entity.type.OrderStatus;
import com.example.tacoshop.exception.BusinessException;
import com.example.tacoshop.exception.ResourceNotFoundException;
import com.example.tacoshop.repository.OrderItemRepository;
import com.example.tacoshop.repository.OrderRepository;
import com.example.tacoshop.service.DiscountCodeService;
import com.example.tacoshop.service.OrderService;
import com.example.tacoshop.service.PaymentService;
import com.example.tacoshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final OrderItemRepository orderItemRepository;
    private final DiscountCodeService discountCodeService;
    private final PaymentService paymentService;
    private final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Override
    @Transactional
    public Long createOrder(User user, CreateOrderRequest request) {
        OrderEntity order = OrderEntity.builder()
                .customer(user)
                .status(OrderStatus.PENDING_PAYMENT)
                .expiresAt(OffsetDateTime.now().plus(ApplicationConstants.ORDER_EXPIRY_DURATION))
                .build();
        List<OrderItem> orderItems = createAndValidateOrderItems(request, order);
        order.setItems(orderItems);
        order.setTotalAmount(calculateTotalAmount(orderItems));
        if (request.discountCode() != null && !request.discountCode().isBlank()) {
            order.setDiscountCode(request.discountCode());
            long discountAmount = discountCodeService.calculateDiscountCode(order);
            order.setDiscountAmount(BigDecimal.valueOf(discountAmount));
        } else {
            order.setDiscountAmount(BigDecimal.ZERO);
        }
        OrderEntity savedOrder = orderRepository.save(order);
        orderItemRepository.saveAll(orderItems);
        return savedOrder.getId();
    }

    @Override
    @Transactional
    public void cancelOrder(OrderEntity order, String reason) {
        logger.info("Cancelling order {} with reason: {}", order.getId(), reason);
        if (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessException("INVALID_ORDER_STATE",
                    String.format("Cannot cancel order in %s state", order.getStatus()));
        }
        if (order.getStatus() == OrderStatus.PAID) {
            paymentService.refundPaymentsForOrder(order, reason);
        }
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productService.updateProduct(product.getId(), new ProductRequest(
                    product.getName(), product.getPrice(), product.getStock(), product.getActive()));
        }
        updateOrderStatus(order, OrderStatus.CANCELLED);
        logger.info("Order {} cancelled successfully", order.getId());
    }

    @Override
    @Transactional
    public void completeOrder(OrderEntity order) {
        if (order.getStatus() != OrderStatus.PAID) {
            throw new IllegalStateException("Only paid orders can be completed");
        }
        updateOrderStatus(order, OrderStatus.COMPLETED);
    }

    @Override
    public PageResponse<OrderResponse> findByCustomerId(Long customerId, Integer page, Integer size) {
        Page<OrderEntity> orderPage = orderRepository.findByCustomerId(customerId, PageRequest.of(page, size));
        List<OrderResponse> orderResponses = orderPage.getContent().stream()
                .map(this::mapToOrderResponse)
                .toList();
        return new PageResponse<>(orderResponses, orderPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderEntity findOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
    }

    @Override
    @Transactional
    public void updateOrderStatus(OrderEntity order, OrderStatus status) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
        order.setStatus(status);
        orderRepository.save(order);
        logger.info("Updated order {} status to {}", order.getId(), status);
    }

    private OrderResponse mapToOrderResponse(OrderEntity order) {
        List<OrderItemResponse> itemResponses = order.getItems() != null
                ? order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getPriceAtOrder(),
                        item.getCustomData()))
                .toList()
                : List.of();
        return new OrderResponse(
                UserMapper.toUserResponse(order.getCustomer()),
                itemResponses,
                order.getTotalAmount(),
                order.getDiscountAmount(),
                order.getDiscountCode(),
                order.getStatus().name(),
                order.getExpiresAt()
        );
    }

    private List<OrderItem> createAndValidateOrderItems(CreateOrderRequest request, OrderEntity order) {
        List<Long> productIds = request.items().stream()
                .map(OrderItemRequest::productId)
                .toList();
        Map<Long, Product> productMap = productService.findAllByIdIn(productIds)
                .stream()
                .collect(Collectors.toMap(Product::getId, p -> p));
        List<OrderItem> orderItemList = request.items().stream()
                .map(item -> {
                    Product product = productMap.get(item.productId());
                    if (product == null) {
                        throw new ResourceNotFoundException("Product", "id", item.productId());
                    }
                    if (product.getStock() < item.quantity()) {
                        throw new BusinessException("INSUFFICIENT_STOCK",
                                String.format("Insufficient stock for product %s. Available: %d, Requested: %d",
                                        product.getName(), product.getStock(), item.quantity()));
                    }
                    return OrderItem.builder()
                            .order(order)
                            .product(product)
                            .quantity(item.quantity())
                            .priceAtOrder(product.getPrice())
                            .customData(item.customData())
                            .build();
                }).collect(Collectors.toList());
        return orderItemList;
    }

    private BigDecimal calculateTotalAmount(List<OrderItem> items) {
        return items.stream()
                .map(item -> item.getPriceAtOrder().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public PageResponse<OrderResponse> findAllOrders(Integer page, Integer size) {
        Page<OrderEntity> orderPage = orderRepository.findAll(PageRequest.of(page, size));
        List<OrderResponse> orderResponses = orderPage.getContent().stream()
                .map(this::mapToOrderResponse)
                .toList();
        return new PageResponse<>(orderResponses, orderPage.getTotalElements());
    }

    @Override
    @Transactional
    public OrderResponse updateOrder(Long id, CreateOrderRequest request, User user) {
        OrderEntity order = findOrderById(id);
        if (!order.getCustomer().getId().equals(user.getId())) {
            throw new BusinessException("FORBIDDEN", "You can only update your own orders");
        }
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BusinessException("INVALID_ORDER_STATE", "Can only update pending orders");
        }
        orderItemRepository.deleteAll(order.getItems());
        List<OrderItem> newItems = createAndValidateOrderItems(request, order);
        order.setItems(newItems);
        order.setTotalAmount(calculateTotalAmount(newItems));
        order.setDiscountCode(request.discountCode());
        order.setDiscountAmount(BigDecimal.valueOf(discountCodeService.calculateDiscountCode(order)));
        order.setExpiresAt(OffsetDateTime.now().plus(ApplicationConstants.ORDER_EXPIRY_DURATION));
        orderRepository.save(order);
        orderItemRepository.saveAll(newItems);
        return mapToOrderResponse(order);
    }

}
