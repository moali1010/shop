package com.example.tacoshop.repository;

import com.example.tacoshop.entity.OrderItem;
import com.example.tacoshop.repository.base.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends BaseRepository<OrderItem, Long> {
}