package com.example.tacoshop.repository;

import com.example.tacoshop.entity.OrderEntity;
import com.example.tacoshop.entity.type.OrderStatus;
import com.example.tacoshop.repository.base.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends BaseRepository<OrderEntity, Long> {

    Page<OrderEntity> findByCustomerId(Long customerId, Pageable pageable);

    List<OrderEntity> findByStatus(OrderStatus status);
}
