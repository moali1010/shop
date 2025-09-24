package com.example.tacoshop.service.discount;

import com.example.tacoshop.entity.DiscountCode;
import com.example.tacoshop.entity.OrderEntity;
import org.springframework.stereotype.Component;

@Component
public class PercentageMaxDiscountStrategy implements DiscountStrategy {

    @Override
    public long applyDiscount(OrderEntity order, DiscountCode code) {
        long discount = order.getTotalAmount().longValue() * code.getValue() / 100;
        return Math.min(discount, code.getMaxAmount());
    }

}
