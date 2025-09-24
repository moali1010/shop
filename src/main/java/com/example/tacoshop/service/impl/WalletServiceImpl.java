package com.example.tacoshop.service.impl;

import com.example.tacoshop.entity.User;
import com.example.tacoshop.entity.Wallet;
import com.example.tacoshop.entity.WalletTransaction;
import com.example.tacoshop.exception.BusinessException;
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
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("INVALID_AMOUNT", "Amount must be positive");
        }
        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException("WALLET_NOT_FOUND", "Wallet not found for user"));
        wallet.setBalance(wallet.getBalance().add(amount));
        WalletTransaction tx = WalletTransaction.builder()
                .wallet(wallet)
                .amount(amount)
                .description(description)
                .build();
        transactionRepository.save(tx);
        walletRepository.save(wallet);
    }

    @Override
    @Transactional
    public void debitWallet(User user, BigDecimal amount, String description) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("INVALID_AMOUNT", "Amount must be positive");
        }
        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException("WALLET_NOT_FOUND", "Wallet not found for user"));
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new BusinessException("INSUFFICIENT_WALLET_FUNDS", "Insufficient wallet balance");
        }
        wallet.setBalance(wallet.getBalance().subtract(amount));
        WalletTransaction tx = WalletTransaction.builder()
                .wallet(wallet)
                .amount(amount.negate())
                .description(description)
                .build();
        transactionRepository.save(tx);
        walletRepository.save(wallet);
    }

    @Override
    public BigDecimal getBalance(User user) {
        return walletRepository.findByUserId(user.getId())
                .map(Wallet::getBalance)
                .orElse(BigDecimal.ZERO);
    }

}
