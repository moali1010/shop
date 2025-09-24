package com.example.tacoshop.controller;

import com.example.tacoshop.dto.request.DiscountRequest;
import com.example.tacoshop.dto.response.DiscountResponse;
import com.example.tacoshop.dto.response.PageResponse;
import com.example.tacoshop.service.DiscountCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/discounts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminDiscountController {

    private final DiscountCodeService discountCodeService;

    @PostMapping
    public ResponseEntity<DiscountResponse> createDiscount(@RequestBody DiscountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(discountCodeService.createDiscount(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiscountResponse> updateDiscount(@PathVariable Long id, @RequestBody DiscountRequest request) {
        return ResponseEntity.ok(discountCodeService.updateDiscount(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateDiscount(@PathVariable Long id) {
        discountCodeService.deactivateDiscount(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<PageResponse<DiscountResponse>> findAllDiscounts(@RequestParam Integer page, @RequestParam Integer size) {
        return ResponseEntity.ok(discountCodeService.findAllDiscounts(page, size));
    }

}
