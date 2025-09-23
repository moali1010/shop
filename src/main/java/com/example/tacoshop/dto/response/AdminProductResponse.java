package com.example.tacoshop.dto.response;

public record AdminProductResponse(
        Long id,
        String name,
        Long price,
        Integer stock,
        Boolean active
) {
}
