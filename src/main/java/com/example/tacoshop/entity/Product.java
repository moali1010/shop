package com.example.tacoshop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "products")
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseModel<Long> {
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private Long price;
    @Column(nullable = false)
    private Integer stock;
}
