package com.example.shop.repository;

import com.example.shop.model.ingredients.Ingredient;
import com.example.shop.model.type.IngredientType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    List<Ingredient> findByType(IngredientType type);
}
