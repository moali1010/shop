package com.example.tacoshop.controller;

import com.example.tacoshop.dto.request.ProductRequest;
import com.example.tacoshop.dto.response.AdminProductResponse;
import com.example.tacoshop.dto.response.PageResponse;
import com.example.tacoshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<AdminProductResponse> createProduct(@RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.saveProduct(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminProductResponse> updateProduct(@PathVariable Long id, @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateProduct(@PathVariable Long id) {
        productService.deactivateProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<PageResponse<AdminProductResponse>> findAllProducts(@RequestParam Integer page, @RequestParam Integer size) {
        return ResponseEntity.ok(productService.findAllWithDeactivate(page, size));
    }

}
