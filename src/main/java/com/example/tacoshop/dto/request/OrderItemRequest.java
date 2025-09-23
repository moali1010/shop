package com.example.tacoshop.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderItemRequest(
        @NotNull(message = "ProductId is required")
        Long productId,
        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be positive")
        Integer quantity,
        String customData
) {
}
