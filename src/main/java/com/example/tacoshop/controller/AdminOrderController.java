package com.example.tacoshop.controller;

import com.example.tacoshop.dto.response.OrderResponse;
import com.example.tacoshop.dto.response.PageResponse;
import com.example.tacoshop.entity.OrderEntity;
import com.example.tacoshop.entity.type.OrderStatus;
import com.example.tacoshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<PageResponse<OrderResponse>> findAllOrders(@RequestParam Integer page, @RequestParam Integer size) {
        return ResponseEntity.ok(orderService.findAllOrders(page, size));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        OrderEntity order = orderService.findOrderById(id);
        orderService.updateOrderStatus(order, status);
        if (status == OrderStatus.COMPLETED) {
            orderService.completeOrder(order);
        }
        return ResponseEntity.ok().build();
    }

}
