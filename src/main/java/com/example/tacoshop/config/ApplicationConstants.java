package com.example.tacoshop.config;

import java.math.BigDecimal;
import java.time.Duration;

public final class ApplicationConstants {

    public static final BigDecimal DEFAULT_CREDIT_LIMIT = BigDecimal.valueOf(100000);
    public static final Duration ORDER_EXPIRY_DURATION = Duration.ofMinutes(30);

    private ApplicationConstants() {
    }

}
