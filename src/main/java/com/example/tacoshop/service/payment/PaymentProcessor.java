package com.example.tacoshop.service.payment;

import com.example.tacoshop.entity.User;

import java.math.BigDecimal;

public interface PaymentProcessor {

    void processPayment(User user, BigDecimal amount, String description);

    void processRefund(User user, BigDecimal amount, String description);

    boolean canProcess(String paymentMethod);
}
