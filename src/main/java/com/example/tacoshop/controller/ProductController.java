package com.example.tacoshop.controller;

import com.example.tacoshop.dto.response.CustomerProductResponse;
import com.example.tacoshop.dto.response.PageResponse;
import com.example.tacoshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<PageResponse<CustomerProductResponse>> findAllProducts(@RequestParam Integer page, @RequestParam Integer size) {
        return ResponseEntity.ok(productService.findAll(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerProductResponse> findProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

}
