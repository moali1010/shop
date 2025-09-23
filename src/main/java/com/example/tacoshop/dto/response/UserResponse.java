package com.example.tacoshop.dto.response;

public record UserResponse(
        Long id,
        String username,
        String role
) {
}
