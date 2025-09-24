package com.example.tacoshop.repository;

import com.example.tacoshop.entity.DiscountCode;
import com.example.tacoshop.repository.base.BaseRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;

@Repository
public interface DiscountCodeRepository extends BaseRepository<DiscountCode, Long> {

    Optional<DiscountCode> findByCodeAndActiveIsTrueAndExpiresAtAfter(String code, OffsetDateTime expiresAt);

    Optional<DiscountCode> findByCodeAndActiveIsTrue(String code);
}