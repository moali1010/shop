package com.example.tacoshop.service;

import com.example.tacoshop.dto.request.DiscountRequest;
import com.example.tacoshop.dto.response.DiscountResponse;
import com.example.tacoshop.dto.response.PageResponse;
import com.example.tacoshop.entity.OrderEntity;

public interface DiscountCodeService {

    long calculateDiscountCode(OrderEntity order);

    DiscountResponse createDiscount(DiscountRequest request);

    DiscountResponse updateDiscount(Long id, DiscountRequest request);

    void deactivateDiscount(Long id);

    PageResponse<DiscountResponse> findAllDiscounts(Integer page, Integer size);

}
