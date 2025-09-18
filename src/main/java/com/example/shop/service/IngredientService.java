package com.example.shop.service;

import com.example.shop.dto.IngredientDTO;
import com.example.shop.model.ingredients.Ingredient;
import com.example.shop.model.type.IngredientType;
import com.example.shop.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    private IngredientDTO toDTO(Ingredient entity) {
        return IngredientDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .price(entity.getPrice())
                .type(entity.getType())
                .build();
    }

    private Ingredient toEntity(IngredientDTO dto) {
        return Ingredient.builder()
                .id(dto.getId())
                .name(dto.getName())
                .price(dto.getPrice())
                .type(dto.getType())
                .build();
    }

    public List<IngredientDTO> getAllIngredients() {
        return ingredientRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<IngredientDTO> getByType(IngredientType type) {
        return ingredientRepository.findByType(type).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public IngredientDTO saveIngredient(IngredientDTO dto) {
        Ingredient entity = toEntity(dto);
        Ingredient saved = ingredientRepository.save(entity);
        return toDTO(saved);
    }

    public void deleteIngredient(Long id) {
        ingredientRepository.deleteById(id);
    }
}