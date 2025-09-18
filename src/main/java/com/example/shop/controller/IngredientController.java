package com.example.shop.controller;

import com.example.shop.dto.IngredientDTO;
import com.example.shop.model.type.IngredientType;
import com.example.shop.service.IngredientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
@RequiredArgsConstructor
@Validated
public class IngredientController {

    private final IngredientService ingredientService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<IngredientDTO> getAll() {
        return ingredientService.getAllIngredients();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public IngredientDTO save(@Valid @RequestBody IngredientDTO dto) {
        return ingredientService.saveIngredient(dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        ingredientService.deleteIngredient(id);
    }

    @GetMapping("/type/{type}")
    public List<IngredientDTO> getByType(@PathVariable String type) {
        IngredientType ingredientType = IngredientType.valueOf(type.toUpperCase());
        return ingredientService.getByType(ingredientType);
    }
}