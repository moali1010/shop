package com.example.tacoshop.service.impl;

import com.example.tacoshop.entity.User;
import com.example.tacoshop.entity.Wallet;
import com.example.tacoshop.repository.WalletRepository;
import com.example.tacoshop.repository.WalletTransactionRepository;
import com.example.tacoshop.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;

    @Override
    @Transactional
    public Wallet createWalletForUser(User user) {
        Wallet wallet = Wallet.builder()
                .user(user)
                .balance(BigDecimal.ZERO)
                .build();
        return walletRepository.save(wallet);
    }

    @Override
    @Transactional
    public void creditWallet(User user, BigDecimal amount, String description) {
    }

    @Override
    @Transactional
    public void debitWallet(User user, BigDecimal amount, String description) {
    }

    @Override
    public BigDecimal getBalance(User user) {
        return null;
    }
}
