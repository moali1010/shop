package com.example.tacoshop.dto.response;

public record CustomerProductResponse(
        Long id,
        String name,
        Long price,
        Integer stock
) {
}
