package com.example.tacoshop.service.discount;

import com.example.tacoshop.entity.type.DiscountType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DiscountStrategyFactory {
    private final PercentageDiscountStrategy percentage;
    private final PercentageMaxDiscountStrategy percentageMax;
    private final FixedAmountDiscountStrategy fixed;

    public DiscountStrategy getStrategy(DiscountType type) {
        return switch (type) {
            case PERCENTAGE -> percentage;
            case PERCENTAGE_MAX -> percentageMax;
            case FIXED_AMOUNT -> fixed;
        };
    }
}
