package com.example.tacoshop.dto.response;

import com.example.tacoshop.entity.type.DiscountType;

import java.time.OffsetDateTime;

public record DiscountResponse(
        Long id,
        String code,
        DiscountType type,
        long value,
        long maxAmount,
        OffsetDateTime expiresAt
) {
}
