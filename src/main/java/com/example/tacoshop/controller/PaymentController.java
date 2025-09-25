package com.example.tacoshop.controller;

import com.example.tacoshop.entity.OrderEntity;
import com.example.tacoshop.entity.PaymentTransaction;
import com.example.tacoshop.entity.User;
import com.example.tacoshop.entity.type.OrderStatus;
import com.example.tacoshop.entity.type.PaymentMethod;
import com.example.tacoshop.security.AppUserDetails;
import com.example.tacoshop.service.CreditService;
import com.example.tacoshop.service.OrderService;
import com.example.tacoshop.service.PaymentService;
import com.example.tacoshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderService orderService;
    private final CreditService creditService;
    private final UserService userService;

    @PostMapping("/order/{orderId}")
    public ResponseEntity<?> payOrder(@PathVariable Long orderId,
                                      @RequestParam PaymentMethod method,
                                      @AuthenticationPrincipal AppUserDetails principal) {
        User user = userService.findByUsername(principal.getUsername());
        OrderEntity order = orderService.findOrderById(orderId);
        if (!order.getCustomer().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("errorCode", "FORBIDDEN",
                            "error", "You can only pay for your own orders"));
        }
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            return ResponseEntity.badRequest()
                    .body(Map.of("errorCode", "INVALID_ORDER_STATE",
                            "error", "Order is not in pending payment status or already paid"));
        }

        if (paymentService.hasSuccessfulPayment(order.getId())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("errorCode", "ALREADY_PAID",
                            "error", "Order already has a successful payment"));
        }
        BigDecimal amount = order.getTotalAmount().subtract(order.getDiscountAmount());
        if (method == PaymentMethod.CREDIT) {
            BigDecimal availableCredit = creditService.getAvailableCredit(user);
            if (availableCredit.compareTo(amount) < 0) {
                BigDecimal remaining = amount.subtract(availableCredit);
                paymentService.initiateOrderPayment(user, order, method, availableCredit);
                String gatewayUrl = "gateway/pay?amount=" + remaining + "&orderId=" + orderId;
                return ResponseEntity.ok(Map.of("message", "Insufficient credit, pay remaining via bank", "remaining", remaining, "gatewayUrl", gatewayUrl));
            }
        }
        PaymentTransaction payment = paymentService.initiateOrderPayment(user, order, method, amount);
        orderService.updateOrderStatus(order, OrderStatus.PAID);
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/wallet/charge")
    public ResponseEntity<Void> chargeWallet(@AuthenticationPrincipal AppUserDetails principal, @RequestParam BigDecimal amount, @RequestParam PaymentMethod method) {
        User user = userService.findByUsername(principal.getUsername());
        paymentService.initiateWalletCharge(user, amount, method);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/credit/repay")
    public ResponseEntity<Void> repayCredit(@AuthenticationPrincipal AppUserDetails principal, @RequestParam BigDecimal amount, @RequestParam PaymentMethod method) {
        User user = userService.findByUsername(principal.getUsername());
        paymentService.repayCredit(user, amount, method);
        return ResponseEntity.ok().build();
    }

}