package com.example.shop.controller;

import com.example.shop.dto.UserDTO;
import com.example.shop.model.type.UserRole;
import com.example.shop.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public UserDTO register(@Valid @RequestBody UserDTO userDTO) {
        userDTO.setUserRole(UserRole.CUSTOMER);
        return userService.saveUser(userDTO);
    }

    @GetMapping("/customers")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDTO> getAllCustomers() {
        return userService.getAllCustomers();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @PutMapping("/profile/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public UserDTO updateProfile(@PathVariable Long id, @Valid @RequestBody UserDTO updatedDTO) {
        return userService.updateProfile(id, updatedDTO);
    }
}