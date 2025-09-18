package com.example.shop.service;

import com.example.shop.dto.DiscountDTO;
import com.example.shop.dto.OrderListDTO;
import com.example.shop.model.discounts.Discount;
import com.example.shop.model.orders.OrderItem;
import com.example.shop.model.orders.OrderList;
import com.example.shop.model.type.DiscountType;
import com.example.shop.repository.DiscountRepository;
import com.example.shop.repository.OrderListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiscountService {

    private final DiscountRepository discountRepository;
    private final OrderListRepository orderListRepository;
    private final OrderService orderService;

    private DiscountDTO toDTO(Discount entity) {
        return DiscountDTO.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .discount(entity.getDiscount())
                .discountType(entity.getDiscountType())
                .isActive(entity.getIsActive())
                .build();
    }

    private Discount toEntity(DiscountDTO dto) {
        return Discount.builder()
                .id(dto.getId())
                .code(dto.getCode())
                .discount(dto.getDiscount())
                .discountType(dto.getDiscountType())
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .build();
    }

    public DiscountDTO saveDiscount(DiscountDTO dto) {
        Discount entity = toEntity(dto);
        Discount saved = discountRepository.save(entity);
        return toDTO(saved);
    }

    public List<DiscountDTO> getActiveDiscounts() {
        return discountRepository.findByIsActiveTrue().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public OrderListDTO applyDiscount(Long orderId, String code) {
        OrderList order = orderListRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        Discount discount = discountRepository.findByCode(code).orElseThrow(() -> new RuntimeException("Invalid discount code"));
        if (!discount.getIsActive()) {
            throw new RuntimeException("Discount not active");
        }
        order.setDiscountCode(code);
        double subtotal = order.getOrderItems().stream().mapToDouble(OrderItem::getTotalPrice).sum();
        if (discount.getDiscountType() == DiscountType.PERCENTAGE) {
            order.setTotalPrice(subtotal * (1 - discount.getDiscount() / 100));
        } else {
            order.setTotalPrice(Math.max(0, subtotal - discount.getDiscount()));
        }
        OrderList saved = orderListRepository.save(order);
        return orderService.toDTO(saved);
    }
}