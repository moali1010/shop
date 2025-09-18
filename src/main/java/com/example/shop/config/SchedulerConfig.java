package com.example.shop.config;

import com.example.shop.model.type.OrderStatus;
import com.example.shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerConfig {

    private final OrderService orderService;

    @Scheduled(fixedRate = 60000)
    public void cancelExpiredOrders() {
        orderService.getExpiredOrders().forEach(order -> {
            order.setStatus(OrderStatus.CANCELLED);
            orderService.updateStatus(order.getId(), OrderStatus.CANCELLED);
        });
        System.out.println("چک منقضی: " + LocalDateTime.now());
    }
}