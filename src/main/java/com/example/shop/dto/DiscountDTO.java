package com.example.shop.dto;

import com.example.shop.model.type.DiscountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountDTO {

    private Long id;

    @NotBlank(message = "Code is required")
    private String code;

    @Positive(message = "Discount must be positive")
    private Double discount;

    private DiscountType discountType;

    private Boolean isActive;
}
