package com.example.tacoshop.controller;

import com.example.tacoshop.dto.mapper.UserMapper;
import com.example.tacoshop.dto.request.UserUpdateRequest;
import com.example.tacoshop.dto.response.UserResponse;
import com.example.tacoshop.entity.User;
import com.example.tacoshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping
    public ResponseEntity<UserResponse> updateProfile(@AuthenticationPrincipal User user, @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userService.updateUser(user.getId(), request));
    }

    @GetMapping
    public ResponseEntity<UserResponse> getProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(UserMapper.toUserResponse(user));
    }

}
