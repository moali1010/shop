package com.example.tacoshop.service;

import com.example.tacoshop.dto.request.ProductRequest;
import com.example.tacoshop.dto.response.AdminProductResponse;
import com.example.tacoshop.dto.response.CustomerProductResponse;
import com.example.tacoshop.dto.response.PageResponse;
import com.example.tacoshop.entity.Product;

import java.util.List;

public interface ProductService {

    AdminProductResponse saveProduct(ProductRequest request);

    PageResponse<AdminProductResponse> findAllWithDeactivate(Integer page, Integer size);

    PageResponse<CustomerProductResponse> findAll(Integer page, Integer size);

    AdminProductResponse updateProduct(Long id, ProductRequest product);

    void deactivateProduct(Long id);

    CustomerProductResponse findById(Long id);

    List<Product> findAllByIdIn(List<Long> ids);
}
