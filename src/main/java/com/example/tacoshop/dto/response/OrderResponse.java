package com.example.tacoshop.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record OrderResponse(
        UserResponse customer,
        List<OrderItemResponse> orderItems,
        BigDecimal totalAmount,
        BigDecimal discountAmount,
        String discountCode,
        String status,
        OffsetDateTime expiresAt
) {
}
