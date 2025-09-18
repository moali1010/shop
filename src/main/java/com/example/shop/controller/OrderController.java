package com.example.shop.controller;

import com.example.shop.dto.OrderListDTO;
import com.example.shop.model.type.OrderStatus;
import com.example.shop.model.users.UserAccount;
import com.example.shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public OrderListDTO createOrder(@RequestBody OrderListDTO orderDTO, Authentication auth) {
        UserAccount customer = (UserAccount) auth.getPrincipal();
        return orderService.createOrder(customer, orderDTO);
    }

    @GetMapping("/my-orders")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<OrderListDTO> getMyOrders(Authentication auth) {
        UserAccount customer = (UserAccount) auth.getPrincipal();
        return orderService.getUserOrders(customer);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<OrderListDTO> getAllOrders() {
        return orderService.getAllOrders();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public OrderListDTO updateStatus(@PathVariable Long id, @RequestBody OrderStatus status) {
        return orderService.updateStatus(id, status);
    }
}