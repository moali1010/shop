package com.example.shop.repository;

import com.example.shop.model.orders.OrderItem;
import com.example.shop.model.orders.OrderList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderList(OrderList orderList);

}
