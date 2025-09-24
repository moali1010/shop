package com.example.tacoshop.service.impl;

import com.example.tacoshop.entity.Credit;
import com.example.tacoshop.entity.User;
import com.example.tacoshop.exception.BusinessException;
import com.example.tacoshop.exception.ResourceNotFoundException;
import com.example.tacoshop.repository.CreditRepository;
import com.example.tacoshop.service.CreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static com.example.tacoshop.config.ApplicationConstants.DEFAULT_CREDIT_LIMIT;

@Service
@RequiredArgsConstructor
public class CreditServiceImpl implements CreditService {

    private final CreditRepository creditRepository;

    @Override
    public Credit createCreditForUser(User user) {
        Credit credit = Credit.builder()
                .user(user)
                .limit(DEFAULT_CREDIT_LIMIT)
                .used(BigDecimal.ZERO)
                .build();
        return creditRepository.save(credit);
    }

    @Override
    @Transactional
    public void debitCredit(User user, BigDecimal amount) {
        Credit credit = creditRepository.findByUserId(user.getId()).orElseThrow(() ->
                new ResourceNotFoundException("CREDIT", "user-id", user.getId())
        );
        BigDecimal available = credit.getLimit().subtract(credit.getUsed());
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("INVALID_AMOUNT", "Amount must be positive");
        }
        if (amount.compareTo(available) > 0) {
            throw new BusinessException("INSUFFICIENT_CREDIT", String.format("Insufficient credit. Available: %s, Requested: %s", available, amount));
        }
        credit.setUsed(credit.getUsed().add(amount));
        creditRepository.save(credit);
    }

    @Override
    @Transactional
    public void repayCredit(User user, BigDecimal amount) {
        Credit credit = creditRepository.findByUserId(user.getId()).orElseThrow(() ->
                new ResourceNotFoundException("CREDIT", "user-id", user.getId())
        );
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("INVALID_AMOUNT", "Amount must be positive");
        }
        credit.setUsed(credit.getUsed().subtract(amount));
        if (credit.getUsed().compareTo(BigDecimal.ZERO) < 0) {
            credit.setUsed(BigDecimal.ZERO);
        }
        creditRepository.save(credit);
    }

    @Override
    public BigDecimal getAvailableCredit(User user) {
        Credit credit = creditRepository.findByUserId(user.getId()).orElseThrow(() ->
                new ResourceNotFoundException("CREDIT", "user-id", user.getId())
        );
        return credit.getLimit().subtract(credit.getUsed());
    }

}
