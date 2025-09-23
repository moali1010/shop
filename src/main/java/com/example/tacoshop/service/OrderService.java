package com.example.tacoshop.service;

import com.example.tacoshop.dto.request.CreateOrderRequest;
import com.example.tacoshop.dto.response.OrderResponse;
import com.example.tacoshop.dto.response.PageResponse;
import com.example.tacoshop.entity.OrderEntity;
import com.example.tacoshop.entity.User;
import com.example.tacoshop.entity.type.OrderStatus;

public interface OrderService {
    Long createOrder(User user, CreateOrderRequest request);

    void cancelOrder(OrderEntity order, String reason);

    void completeOrder(OrderEntity order);

    OrderEntity findOrderById(Long id);

    PageResponse<OrderResponse> findByCustomerId(Long customerId, Integer page, Integer size);

    void updateOrderStatus(OrderEntity order, OrderStatus status);
}
