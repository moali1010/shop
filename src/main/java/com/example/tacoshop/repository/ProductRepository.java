package com.example.tacoshop.repository;

import com.example.tacoshop.entity.Product;
import com.example.tacoshop.repository.base.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends BaseRepository<Product, Long> {
    List<Product> findAllByIdInAndActiveIsTrue(List<Long> ids);
}
