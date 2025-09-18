package com.example.shop.dto;

import com.example.shop.model.ingredients.Ingredient;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {

    private Long id;

    private Long orderListId;

    private Integer quantity;

    private List<Ingredient> ingredients;

    private Double totalPrice;
}
