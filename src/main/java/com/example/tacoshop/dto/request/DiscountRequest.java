package com.example.tacoshop.dto.request;

import com.example.tacoshop.entity.type.DiscountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.OffsetDateTime;

public record DiscountRequest(
        @NotBlank(message = "Discount code is required")
        String code,
        @NotNull(message = "Discount type is required")
        DiscountType type,
        @NotNull(message = "Value is required")
        @PositiveOrZero(message = "Value must be positive or zero")
        long value,
        @PositiveOrZero(message = "Max amount must be positive or zero")
        long maxAmount,
        @NotNull(message = "Expiration date is required")
        OffsetDateTime expiresAt
) {
}
