package com.example.shop.model.discounts;

import com.example.shop.model.type.DiscountType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private Double discount;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    private Boolean isActive = true;
}

