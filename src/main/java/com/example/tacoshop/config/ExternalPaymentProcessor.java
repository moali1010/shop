package com.example.tacoshop.config;

import com.example.tacoshop.entity.User;
import com.example.tacoshop.service.payment.PaymentProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class ExternalPaymentProcessor implements PaymentProcessor {

    @Override
    public void processPayment(User user, BigDecimal amount, String description) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        boolean success = ThreadLocalRandom.current().nextInt(100) < 95;
        if (!success) {
            throw new RuntimeException("External gateway payment failure");
        }
    }

    @Override
    public void processRefund(User user, BigDecimal amount, String description) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        boolean success = ThreadLocalRandom.current().nextInt(100) < 98;
        if (!success) {
            throw new RuntimeException("External gateway refund failure");
        }
    }

    @Override
    public boolean canProcess(String paymentMethod) {
        return "ZARINPAL".equals(paymentMethod) || "ASANPARDAKHT".equals(paymentMethod);
    }

}
