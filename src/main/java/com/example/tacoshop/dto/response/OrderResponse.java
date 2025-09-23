package com.example.tacoshop.dto.response;

import java.time.OffsetDateTime;
import java.util.List;

public record OrderResponse(
        UserResponse customer,
        List<OrderItemResponse> orderItems,
        Long totalAmount,
        Long discountAmount,
        String discountCode,
        String status,
        OffsetDateTime expiresAt
) {
}
