package com.example.tacoshop.dto.request;

import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateOrderRequest(
        List<OrderItemRequest> items,
        @Size(max = 50, message = "Discount code must be less than 50 characters")
        String discountCode
) {
}
