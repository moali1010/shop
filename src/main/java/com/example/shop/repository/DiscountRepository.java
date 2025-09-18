package com.example.shop.repository;

import com.example.shop.model.discounts.Discount;
import com.example.shop.model.type.DiscountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {

    Optional<Discount> findByCode(String code);

    List<Discount> findByIsActiveTrue();

    List<Discount> findByDiscountType(DiscountType type);
}
