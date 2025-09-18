package com.example.shop.model.orders;

import com.example.shop.model.type.OrderStatus;
import com.example.shop.model.users.UserAccount;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private UserAccount customer;

    @OneToMany(mappedBy = "orderList", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;

    private LocalDateTime orderTime;
    private LocalDateTime expirationTime;
    private String discountCode;
    private Double totalPrice;

    @PrePersist
    void orderTime() {
        this.orderTime = LocalDateTime.now();
        this.expirationTime = this.orderTime.plusHours(1);
    }

    public void calculateTotalPrice() {
        double subtotal = orderItems.stream().mapToDouble(OrderItem::getTotalPrice).sum();
        this.totalPrice = subtotal;
    }
}