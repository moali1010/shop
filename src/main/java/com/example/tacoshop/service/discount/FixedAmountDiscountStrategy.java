package com.example.tacoshop.service.discount;

import com.example.tacoshop.entity.DiscountCode;
import com.example.tacoshop.entity.OrderEntity;
import org.springframework.stereotype.Component;

@Component
public class FixedAmountDiscountStrategy implements DiscountStrategy {

    @Override
    public long applyDiscount(OrderEntity order, DiscountCode code) {
        long total = order.getTotalAmount().longValue();
        if (code.getValue() > total) {
            return total;
        }
        return code.getValue();
    }

}
