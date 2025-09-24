package com.example.tacoshop.service;

import com.example.tacoshop.entity.Credit;
import com.example.tacoshop.entity.User;

import java.math.BigDecimal;

public interface CreditService {

    Credit createCreditForUser(User user);

    void debitCredit(User user, BigDecimal amount);

    void repayCredit(User user, BigDecimal amount);

    BigDecimal getAvailableCredit(User user);
}
