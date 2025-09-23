package com.example.tacoshop.dto.response;

public record OrderItemResponse(
        Long productId,
        String productName,
        Integer quantity,
        Long priceAtOrder,
        String customData
) {
}
