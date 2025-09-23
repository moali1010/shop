package com.example.tacoshop.service.payment;

import com.example.tacoshop.entity.User;
import com.example.tacoshop.service.CreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class CreditPaymentProcessor implements PaymentProcessor {

    private final CreditService creditService;

    @Override
    public void processPayment(User user, BigDecimal amount, String description) {
        creditService.useCredit(user, amount);
    }

    @Override
    public void processRefund(User user, BigDecimal amount, String description) {
        creditService.repayCredit(user, amount);
    }

    @Override
    public boolean canProcess(String paymentMethod) {
        return "CREDIT".equals(paymentMethod);
    }

}
