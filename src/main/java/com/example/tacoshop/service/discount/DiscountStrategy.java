package com.example.tacoshop.service.discount;

import com.example.tacoshop.entity.DiscountCode;
import com.example.tacoshop.entity.OrderEntity;

public interface DiscountStrategy {

    long applyDiscount(OrderEntity order, DiscountCode code);
}
