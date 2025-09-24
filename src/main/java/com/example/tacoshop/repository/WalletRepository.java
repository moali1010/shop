package com.example.tacoshop.repository;

import com.example.tacoshop.entity.Wallet;
import com.example.tacoshop.repository.base.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends BaseRepository<Wallet, Long> {

    Optional<Wallet> findByUserId(Long userId);
}
