package com.example.tacoshop.repository;

import com.example.tacoshop.entity.PaymentTransaction;
import com.example.tacoshop.entity.type.PaymentStatus;
import com.example.tacoshop.repository.base.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends BaseRepository<PaymentTransaction, Long> {

    List<PaymentTransaction> findByOrderId(Long orderId);

    Optional<PaymentTransaction> findByOrderIdAndStatus(Long orderId, PaymentStatus status);
}
