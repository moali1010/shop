package com.example.tacoshop.config;

import com.example.tacoshop.entity.OrderEntity;
import com.example.tacoshop.entity.type.OrderStatus;
import com.example.tacoshop.repository.OrderRepository;
import com.example.tacoshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderExpiryScheduler {

    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final Logger logger = LoggerFactory.getLogger(OrderExpiryScheduler.class);

    @Scheduled(fixedRate = 60000)
    public void expireOrders() {
        List<OrderEntity> pendingOrders = orderRepository.findByStatus(OrderStatus.PENDING_PAYMENT);
        OffsetDateTime now = OffsetDateTime.now();
        for (OrderEntity order : pendingOrders) {
            if (order.getExpiresAt().isBefore(now)) {
                logger.info("Expiring order {}", order.getId());
                orderService.updateOrderStatus(order, OrderStatus.EXPIRED);
            }
        }
    }

}
