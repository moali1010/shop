package com.example.tacoshop.config;

import com.example.tacoshop.entity.User;
import com.example.tacoshop.service.payment.PaymentProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ExternalPaymentProcessor implements PaymentProcessor {

    @Override
    public void processPayment(User user, BigDecimal amount, String description) {
        throw new UnsupportedOperationException("External payment not implemented yet");
    }

    @Override
    public void processRefund(User user, BigDecimal amount, String description) {
        throw new UnsupportedOperationException("External refund not implemented yet");
    }

    @Override
    public boolean canProcess(String paymentMethod) {
        return "ZARINPAL".equals(paymentMethod) || "ASANPARDAKHT".equals(paymentMethod);
    }

}
