package com.example.tacoshop.service.discount;

import com.example.tacoshop.entity.DiscountCode;
import com.example.tacoshop.entity.OrderEntity;
import org.springframework.stereotype.Component;

@Component
public class PercentageDiscountStrategy implements DiscountStrategy {

    @Override
    public long applyDiscount(OrderEntity order, DiscountCode code) {
        return order.getTotalAmount() * code.getValue() / 100;
    }

}
