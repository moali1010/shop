package com.example.tacoshop.controller;

import com.example.tacoshop.dto.request.CreateOrderRequest;
import com.example.tacoshop.dto.response.OrderResponse;
import com.example.tacoshop.dto.response.PageResponse;
import com.example.tacoshop.entity.OrderEntity;
import com.example.tacoshop.entity.User;
import com.example.tacoshop.exception.BusinessException;
import com.example.tacoshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Long> createOrder(@AuthenticationPrincipal User user, @RequestBody CreateOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(user, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> updateOrder(@PathVariable Long id, @AuthenticationPrincipal User user, @RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(orderService.updateOrder(id, request, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id, @AuthenticationPrincipal User user, @RequestParam String reason) {
        OrderEntity order = orderService.findOrderById(id);
        if (!order.getCustomer().getId().equals(user.getId())) {
            throw new BusinessException("FORBIDDEN", "You can only cancel your own orders");
        }
        orderService.cancelOrder(order, reason);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<PageResponse<OrderResponse>> findMyOrders(@AuthenticationPrincipal User user, @RequestParam Integer page, @RequestParam Integer size) {
        return ResponseEntity.ok(orderService.findByCustomerId(user.getId(), page, size));
    }

}
