package com.example.tacoshop.dto.mapper;

import com.example.tacoshop.dto.response.UserResponse;
import com.example.tacoshop.entity.User;

public class UserMapper {

    public static UserResponse toUserResponse(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getRole().name());
    }

}
