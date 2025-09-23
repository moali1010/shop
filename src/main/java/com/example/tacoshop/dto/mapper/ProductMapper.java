package com.example.tacoshop.dto.mapper;

import com.example.tacoshop.dto.response.AdminProductResponse;
import com.example.tacoshop.dto.response.CustomerProductResponse;
import com.example.tacoshop.entity.Product;

public class ProductMapper {
    public static CustomerProductResponse toCustomerProductResponse(Product product) {
        return new CustomerProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock());
    }

    public static AdminProductResponse toAdminProductResponse(Product product) {
        return new AdminProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getActive()
        );
    }
}
