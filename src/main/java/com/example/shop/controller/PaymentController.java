package com.example.shop.controller;

import com.example.shop.dto.PaymentDTO;
import com.example.shop.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public PaymentDTO create(@PathVariable Long orderId, @RequestParam String transactionId) {
        return paymentService.createPayment(orderId, transactionId);
    }

    @PostMapping("/confirm")
    public void confirm(@RequestParam String transactionId) {
        paymentService.confirmPayment(transactionId);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<PaymentDTO> getAllPayments() {
        return paymentService.getAllPayments();
    }
}
