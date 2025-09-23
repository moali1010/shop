package com.example.tacoshop.dto;

import com.example.tacoshop.dto.response.UserResponse;

public record AuthResponse(
        String token,
        UserResponse user
) {
}
