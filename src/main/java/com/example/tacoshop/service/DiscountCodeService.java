package com.example.tacoshop.service;

import com.example.tacoshop.entity.OrderEntity;

public interface DiscountCodeService {
    long calculateDiscountCode(OrderEntity order);
}
