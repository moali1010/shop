package com.example.tacoshop.config;


import java.math.BigDecimal;
import java.time.Duration;

public final class ApplicationConstants {

    public static final BigDecimal DEFAULT_CREDIT_LIMIT = BigDecimal.valueOf(1000000);
    public static final Duration ORDER_EXPIRY_DURATION = Duration.ofMinutes(30);
    public static final BigDecimal WALLET_DISCOUNT_PERCENT = BigDecimal.valueOf(0.04); // 4%

    private ApplicationConstants() {
    }

}
