package com.example.shop.controller;

import com.example.shop.dto.DiscountDTO;
import com.example.shop.dto.OrderListDTO;
import com.example.shop.service.DiscountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discounts")
@RequiredArgsConstructor
@Validated
public class DiscountController {

    private final DiscountService discountService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public DiscountDTO create(@Valid @RequestBody DiscountDTO dto) {
        return discountService.saveDiscount(dto);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<DiscountDTO> getActive() {
        return discountService.getActiveDiscounts();
    }

    @PostMapping("/apply/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public OrderListDTO apply(@PathVariable Long orderId, @RequestParam String code) {
        return discountService.applyDiscount(orderId, code);
    }
}