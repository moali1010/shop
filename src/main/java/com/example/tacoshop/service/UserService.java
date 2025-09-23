package com.example.tacoshop.service;

import com.example.tacoshop.dto.AuthResponse;
import com.example.tacoshop.dto.request.UserLoginRequest;
import com.example.tacoshop.dto.request.UserRegistrationRequest;
import com.example.tacoshop.dto.response.UserResponse;
import com.example.tacoshop.entity.User;

public interface UserService {

    UserResponse registerUser(UserRegistrationRequest request);

    AuthResponse loginUser(UserLoginRequest request);

    User findByUsername(String username);
}
