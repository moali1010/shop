package com.example.tacoshop.controller;

import com.example.tacoshop.dto.request.UserUpdateRequest;
import com.example.tacoshop.dto.response.PageResponse;
import com.example.tacoshop.dto.response.UserResponse;
import com.example.tacoshop.entity.User;
import com.example.tacoshop.service.CreditService;
import com.example.tacoshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminUserController {

    private final UserService userService;
    private final CreditService creditService;

    @GetMapping
    public ResponseEntity<PageResponse<UserResponse>> findAllCustomers(@RequestParam Integer page, @RequestParam Integer size) {
        return ResponseEntity.ok(userService.findAllCustomers(page, size));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/credit-limit")
    public ResponseEntity<Void> updateUserCreditLimit(@PathVariable Long id, @RequestParam java.math.BigDecimal limit) {
        User user = userService.findById(id);
        creditService.setCreditLimit(user, limit);
        return ResponseEntity.ok().build();
    }

}
