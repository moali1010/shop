package com.example.tacoshop.controller;

import com.example.tacoshop.dto.request.ProductRequest;
import com.example.tacoshop.dto.response.AdminProductResponse;
import com.example.tacoshop.dto.response.PageResponse;
import com.example.tacoshop.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ProductService productService;

    @PostMapping("/products")
    public ResponseEntity<AdminProductResponse> addProduct(@Valid @RequestBody ProductRequest request) {
        AdminProductResponse savedProduct = productService.saveProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    @GetMapping("/products")
    public ResponseEntity<PageResponse<AdminProductResponse>> listProducts(@RequestParam Integer page,
                                                                           @RequestParam Integer size) {
        PageResponse<AdminProductResponse> products = productService.findAllWithDeactivate(page, size);
        return ResponseEntity.ok(products);
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<AdminProductResponse> updateProduct(@PathVariable Long id,
                                                              @Valid @RequestBody ProductRequest product) {
        AdminProductResponse updatedProduct = productService.updateProduct(id, product);
        return ResponseEntity.ok(updatedProduct);

    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully");
    }

}
