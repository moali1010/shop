package com.example.tacoshop.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    @NotNull(message = "Total amount is required")
    @PositiveOrZero(message = "Total amount must be positive or zero")
    private Long totalAmount;
    @PositiveOrZero(message = "Discount amount must be positive or zero")
    private Long discountAmount = 0L;
    @Size(max = 50, message = "Discount code must be less than 50 characters")
    private String discountCode;
    @Valid
    private List<OrderItemRequest> items;

    @Data
    public static class OrderItemRequest {
        @NotNull(message = "Product ID is required")
        private Long productId;
        @NotNull(message = "Quantity is required")
        @PositiveOrZero(message = "Quantity must be positive")
        private Integer quantity;
        private String customData;
    }
}
