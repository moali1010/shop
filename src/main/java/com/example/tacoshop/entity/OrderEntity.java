package com.example.tacoshop.entity;

import com.example.tacoshop.entity.type.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity extends BaseModel<Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderItem> items;
    @Column(nullable = false)
    private Long totalAmount;
    @Column(nullable = false)
    @Builder.Default
    private Long discountAmount = 0L;
    private String discountCode;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;
    private OffsetDateTime expiresAt;
}
