package com.example.tacoshop.dto.response;

import java.math.BigDecimal;

public record AdminProductResponse(
        Long id,
        String name,
        BigDecimal price,
        Integer stock,
        Boolean active
) {
}
