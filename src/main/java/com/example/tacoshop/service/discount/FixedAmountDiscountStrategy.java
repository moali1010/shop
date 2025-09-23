package com.example.tacoshop.service.discount;

import com.example.tacoshop.entity.DiscountCode;
import com.example.tacoshop.entity.OrderEntity;
import org.springframework.stereotype.Component;

@Component
public class FixedAmountDiscountStrategy implements DiscountStrategy {
    @Override
    public long applyDiscount(OrderEntity order, DiscountCode code) {
        if (code.getValue() > order.getTotalAmount()) {
            return order.getTotalAmount();
        }
        return code.getValue();
    }
}
