package com.example.tacoshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "credits")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Credit extends BaseModel<Long> {

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(nullable = false, name = "credit_max_limit")
    private BigDecimal limit = BigDecimal.ZERO;
    @Column(nullable = false)
    private BigDecimal used = BigDecimal.ZERO;

}
