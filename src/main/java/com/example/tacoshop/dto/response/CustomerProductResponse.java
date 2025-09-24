package com.example.tacoshop.dto.response;

import java.math.BigDecimal;

public record CustomerProductResponse(
        Long id,
        String name,
        BigDecimal price,
        Integer stock
) {
}
