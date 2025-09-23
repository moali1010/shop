package com.example.tacoshop.entity;

import com.example.tacoshop.entity.type.PaymentMethod;
import com.example.tacoshop.entity.type.PaymentPurpose;
import com.example.tacoshop.entity.type.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "payments")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransaction extends BaseModel<Long> {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private OrderEntity order;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private PaymentPurpose purpose;
    @Enumerated(EnumType.STRING)
    private PaymentMethod method;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    private String providerRef;
}
