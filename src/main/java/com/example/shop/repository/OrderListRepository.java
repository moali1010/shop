package com.example.shop.repository;

import com.example.shop.model.orders.OrderList;
import com.example.shop.model.type.OrderStatus;
import com.example.shop.model.users.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderListRepository extends JpaRepository<OrderList, Long> {

    List<OrderList> findByCustomer(UserAccount customer);

    List<OrderList> findByCustomerAndStatus(UserAccount customer, OrderStatus status);

    List<OrderList> findAllByStatus(OrderStatus status);

    @Query("SELECT o FROM OrderList o WHERE o.status = 'PENDING' AND o.expirationTime < :now")
    List<OrderList> findExpiredOrders(@Param("now") LocalDateTime now);
}
