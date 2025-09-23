package com.example.tacoshop.dto.response;

import java.util.List;

public record PageResponse<T>(
        List<T> data,
        Long totalElements
) {
}
