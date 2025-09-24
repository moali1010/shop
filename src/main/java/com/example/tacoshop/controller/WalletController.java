package com.example.tacoshop.controller;

import com.example.tacoshop.entity.User;
import com.example.tacoshop.entity.type.PaymentMethod;
import com.example.tacoshop.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/user/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final PaymentService paymentService;

    @PostMapping("/charge")
    public ResponseEntity<String> chargeWallet(@RequestParam BigDecimal amount, @RequestParam PaymentMethod method, @AuthenticationPrincipal User user) {
        if (!method.equals(PaymentMethod.ZARINPAL) && !method.equals(PaymentMethod.ASANPARDAKHT)) {
            return ResponseEntity.badRequest().body("Only bank methods allowed for charging");
        }
        paymentService.initiateWalletCharge(user, amount, method);
        return ResponseEntity.ok("Wallet charged successfully");
    }

}
