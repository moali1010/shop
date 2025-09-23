package com.example.tacoshop.repository;

import com.example.tacoshop.entity.Wallet;
import com.example.tacoshop.repository.base.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends BaseRepository<Wallet, Long> {
}
