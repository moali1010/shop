package com.example.tacoshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "wallets")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Wallet extends BaseModel<Long> {
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;
    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL)
    private List<WalletTransaction> transactions;
}
