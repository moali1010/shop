package com.example.shop.model.payments;

import com.example.shop.model.orders.OrderList;
import com.example.shop.model.type.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment", uniqueConstraints = @UniqueConstraint(columnNames = "transaction_id"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_list_id", nullable = false)
    private OrderList orderList;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.PENDING;

    private Double amount;

    private LocalDateTime paymentTime;
    private String transactionId;

    @PrePersist
    void paymentTime() {
        if (this.paymentTime == null) {
            this.paymentTime = LocalDateTime.now();
        }
    }
}