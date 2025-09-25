package com.example.tacoshop.service;

import com.example.tacoshop.entity.User;
import com.example.tacoshop.entity.Wallet;

import java.math.BigDecimal;

public interface WalletService {

    Wallet createWalletForUser(User user);

    void creditWallet(User user, BigDecimal amount, String description);

    void debitWallet(User user, BigDecimal amount, String description);

    BigDecimal getBalance(User user);

}
