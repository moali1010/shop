package com.example.tacoshop.controller;

import com.example.tacoshop.entity.OrderEntity;
import com.example.tacoshop.entity.PaymentTransaction;
import com.example.tacoshop.entity.User;
import com.example.tacoshop.entity.type.OrderStatus;
import com.example.tacoshop.entity.type.PaymentMethod;
import com.example.tacoshop.exception.BusinessException;
import com.example.tacoshop.service.OrderService;
import com.example.tacoshop.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/user/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderService orderService;

    @PostMapping("/order/{orderId}")
    public ResponseEntity<?> payOrder(@PathVariable Long orderId,
                                      @RequestParam PaymentMethod method,
                                      @AuthenticationPrincipal User user) {
        try {
            OrderEntity order = orderService.findOrderById(orderId);
            if (!order.getCustomer().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("errorCode", "FORBIDDEN",
                                "error", "You can only pay for your own orders"));
            }
            if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
                return ResponseEntity.badRequest()
                        .body(Map.of("errorCode", "INVALID_ORDER_STATE",
                                "error", "Order is not in pending payment status"));
            }
            BigDecimal amount = BigDecimal.valueOf(order.getTotalAmount() - order.getDiscountAmount());
            PaymentTransaction payment = paymentService.initiateOrderPayment(user, order, method, amount);
            orderService.updateOrderStatus(order, OrderStatus.PAID);
            return ResponseEntity.ok(payment);
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
