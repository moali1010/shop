package com.example.shop.service;

import com.example.shop.dto.OrderItemDTO;
import com.example.shop.dto.OrderListDTO;
import com.example.shop.model.orders.OrderItem;
import com.example.shop.model.orders.OrderList;
import com.example.shop.model.type.OrderStatus;
import com.example.shop.model.users.UserAccount;
import com.example.shop.repository.OrderItemRepository;
import com.example.shop.repository.OrderListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderListRepository orderListRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderListDTO toDTO(OrderList entity) {
        return OrderListDTO.builder()
                .id(entity.getId())
                .customerId(entity.getCustomer().getId())
                .orderItems(entity.getOrderItems().stream().map(this::toItemDTO).collect(Collectors.toList()))
                .status(entity.getStatus())
                .orderTime(entity.getOrderTime())
                .expirationTime(entity.getExpirationTime())
                .discountCode(entity.getDiscountCode())
                .totalPrice(entity.getTotalPrice())
                .build();
    }

    private OrderItemDTO toItemDTO(OrderItem entity) {
        return OrderItemDTO.builder()
                .id(entity.getId())
                .orderListId(entity.getOrderList().getId())
                .quantity(entity.getQuantity())
                .ingredients(entity.getIngredients())
                .totalPrice(entity.getTotalPrice())
                .build();
    }

    private OrderList toEntity(OrderListDTO dto, UserAccount customer) {
        List<OrderItem> items = dto.getOrderItems().stream()
                .map(itemDTO -> {
                    OrderItem item = OrderItem.builder()
                            .quantity(itemDTO.getQuantity())
                            .ingredients(itemDTO.getIngredients())
                            .build();
                    item.calculateTotalPrice();
                    return orderItemRepository.save(item);
                })
                .toList();
        OrderList order = OrderList.builder()
                .customer(customer)
                .orderItems(items)
                .status(dto.getStatus() != null ? dto.getStatus() : OrderStatus.PENDING)
                .discountCode(dto.getDiscountCode())
                .build();
        items.forEach(item -> item.setOrderList(order));
        order.calculateTotalPrice();
        return order;
    }

    public OrderListDTO createOrder(UserAccount customer, OrderListDTO orderDTO) {
        OrderList order = toEntity(orderDTO, customer);
        OrderList saved = orderListRepository.save(order);
        return toDTO(saved);
    }

    public List<OrderListDTO> getUserOrders(UserAccount customer) {
        return orderListRepository.findByCustomer(customer).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public OrderListDTO updateStatus(Long orderId, OrderStatus newStatus) {
        OrderList order = orderListRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(newStatus);
        if (order.getStatus() == OrderStatus.PENDING && LocalDateTime.now().isAfter(order.getExpirationTime())) {
            order.setStatus(OrderStatus.CANCELLED);
        }
        order.calculateTotalPrice();
        OrderList saved = orderListRepository.save(order);
        return toDTO(saved);
    }

    public List<OrderListDTO> getAllOrders() {
        return orderListRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<OrderListDTO> getExpiredOrders() {
        return orderListRepository.findExpiredOrders(LocalDateTime.now()).stream().map(this::toDTO).collect(Collectors.toList());
    }
}