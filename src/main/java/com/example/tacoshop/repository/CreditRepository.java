package com.example.tacoshop.repository;

import com.example.tacoshop.entity.Credit;
import com.example.tacoshop.repository.base.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CreditRepository extends BaseRepository<Credit, Long> {
    Optional<Credit> findByUserId(Long id);
}
