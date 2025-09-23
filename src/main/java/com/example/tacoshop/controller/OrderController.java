package com.example.tacoshop.controller;

import com.example.tacoshop.dto.request.CreateOrderRequest;
import com.example.tacoshop.dto.response.OrderResponse;
import com.example.tacoshop.dto.response.PageResponse;
import com.example.tacoshop.entity.OrderEntity;
import com.example.tacoshop.entity.User;
import com.example.tacoshop.exception.BusinessException;
import com.example.tacoshop.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Long> createOrder(@Valid @RequestBody CreateOrderRequest order,
                                            @AuthenticationPrincipal User user) {
        Long id = orderService.createOrder(user, order);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @GetMapping
    public ResponseEntity<PageResponse<OrderResponse>> getOrders(@RequestParam Integer page,
                                                                 @RequestParam Integer size,
                                                                 @AuthenticationPrincipal User user) {
        PageResponse<OrderResponse> orders = orderService.findByCustomerId(user.getId(), page, size);
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Map<String, String>> cancelOrder(@PathVariable Long id,
                                                           @AuthenticationPrincipal User user,
                                                           @RequestBody(required = false) Map<String, String> body) {
        try {
            OrderEntity order = orderService.findOrderById(id);
            if (!order.getCustomer().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("errorCode", "FORBIDDEN",
                                "error", "You can only cancel your own orders"));
            }

            String reason = body != null && body.containsKey("reason") ? body.get("reason") : "Cancelled by user";
            orderService.cancelOrder(order, reason);
            return ResponseEntity.ok(Map.of("message", "Order cancelled successfully"));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("errorCode", e.getErrorCode(), "error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("errorCode", "SERVER_ERROR",
                            "error", "An unexpected error occurred"));
        }
    }

}
