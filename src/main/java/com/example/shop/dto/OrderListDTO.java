package com.example.shop.dto;

import com.example.shop.model.type.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderListDTO {

    private Long id;

    private Long customerId;

    private List<OrderItemDTO> orderItems;

    private OrderStatus status;

    private LocalDateTime orderTime;

    private LocalDateTime expirationTime;

    private String discountCode;

    private Double totalPrice;
}
