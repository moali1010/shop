package com.example.tacoshop.exception;

import java.math.BigDecimal;

public class InsufficientFundsException extends BusinessException {

    public InsufficientFundsException(String accountType, BigDecimal available, BigDecimal required) {
        super("INSUFFICIENT_FUNDS",
                String.format("Insufficient %s balance. Available: %s, Required: %s",
                        accountType, available, required));
    }

}
