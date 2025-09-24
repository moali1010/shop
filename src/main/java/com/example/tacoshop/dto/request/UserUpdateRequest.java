package com.example.tacoshop.dto.request;

import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        String username
) {
}
