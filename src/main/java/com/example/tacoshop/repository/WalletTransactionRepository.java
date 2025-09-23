package com.example.tacoshop.repository;

import com.example.tacoshop.entity.WalletTransaction;
import com.example.tacoshop.repository.base.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletTransactionRepository extends BaseRepository<WalletTransaction, Long> {
}
