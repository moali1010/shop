package com.example.tacoshop.service.impl;

import com.example.tacoshop.dto.mapper.ProductMapper;
import com.example.tacoshop.dto.request.ProductRequest;
import com.example.tacoshop.dto.response.AdminProductResponse;
import com.example.tacoshop.dto.response.CustomerProductResponse;
import com.example.tacoshop.dto.response.PageResponse;
import com.example.tacoshop.entity.Product;
import com.example.tacoshop.exception.ResourceNotFoundException;
import com.example.tacoshop.repository.ProductRepository;
import com.example.tacoshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public AdminProductResponse saveProduct(ProductRequest request) {
        Product savedProduct = productRepository.save(Product.builder()
                .name(request.name())
                .price(request.price())
                .stock(request.stock())
                .active(request.isActive())
                .build());
        return ProductMapper.toAdminProductResponse(savedProduct);
    }

    @Override
    public PageResponse<AdminProductResponse> findAllWithDeactivate(Integer page, Integer size) {
        Page<AdminProductResponse> productResponses = productRepository.findAll(PageRequest.of(page, size))
                .map(ProductMapper::toAdminProductResponse);
        return new PageResponse<>(
                productResponses.getContent(), productResponses.getTotalElements()
        );
    }

    @Override
    public PageResponse<CustomerProductResponse> findAll(Integer page, Integer size) {
        Page<CustomerProductResponse> productResponses = productRepository.findAllByActiveIsTrue(PageRequest.of(page, size))
                .map(ProductMapper::toCustomerProductResponse);
        return new PageResponse<>(
                productResponses.getContent(), productResponses.getTotalElements()
        );
    }

    @Override
    @Transactional
    public AdminProductResponse updateProduct(Long id, ProductRequest request) {
        Product foundedProduct = productRepository.findByIdAndActiveIsTrue(id).orElseThrow(() ->
                new ResourceNotFoundException("Product", "id", id));
        foundedProduct.setName(request.name());
        foundedProduct.setPrice(request.price());
        foundedProduct.setStock(request.stock());
        foundedProduct.setActive(request.isActive());
        Product updatedProduct = productRepository.save(foundedProduct);
        return ProductMapper.toAdminProductResponse(updatedProduct);
    }

    @Override
    public void deactivateProduct(Long id) {
        Product product = productRepository.findByIdAndActiveIsTrue(id).orElseThrow(
                () -> new ResourceNotFoundException("Product", "id", id)
        );
        product.setActive(false);
        productRepository.save(product);
    }

    @Override
    public CustomerProductResponse findById(Long id) {
        Product product = productRepository.findByIdAndActiveIsTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return ProductMapper.toCustomerProductResponse(product);
    }

    @Override
    public List<Product> findAllByIdIn(List<Long> ids) {
        return productRepository.findAllByIdInAndActiveIsTrue(ids);
    }

}