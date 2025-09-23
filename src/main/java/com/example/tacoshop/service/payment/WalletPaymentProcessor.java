package com.example.tacoshop.service.payment;

import com.example.tacoshop.entity.User;
import com.example.tacoshop.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class WalletPaymentProcessor implements PaymentProcessor {

    private final WalletService walletService;

    @Override
    public void processPayment(User user, BigDecimal amount, String description) {
        walletService.debitWallet(user, amount, description);
    }

    @Override
    public void processRefund(User user, BigDecimal amount, String description) {
        walletService.creditWallet(user, amount, description);
    }

    @Override
    public boolean canProcess(String paymentMethod) {
        return "WALLET".equals(paymentMethod);
    }

}
