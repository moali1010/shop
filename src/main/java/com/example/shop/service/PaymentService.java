package com.example.shop.service;

import com.example.shop.dto.PaymentDTO;
import com.example.shop.model.orders.OrderList;
import com.example.shop.model.payments.Payment;
import com.example.shop.model.type.OrderStatus;
import com.example.shop.model.type.PaymentStatus;
import com.example.shop.repository.OrderListRepository;
import com.example.shop.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderListRepository orderListRepository;

    private PaymentDTO toDTO(Payment entity) {
        return PaymentDTO.builder()
                .id(entity.getId())
                .orderListId(entity.getOrderList() != null ? entity.getOrderList().getId() : null)
                .amount(entity.getAmount())
                .status(entity.getStatus())
                .paymentTime(entity.getPaymentTime())
                .transactionId(entity.getTransactionId())
                .build();
    }

    public PaymentDTO createPayment(Long orderId, String transactionId) {
        OrderList order = orderListRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Order not pending");
        }
        if (paymentRepository.findByOrderList(order).isPresent()) {
            throw new RuntimeException("Payment already exists");
        }
        if (paymentRepository.findByTransactionId(transactionId).isPresent()) {
            throw new RuntimeException("Duplicate transaction");
        }
        Payment payment = Payment.builder()
                .orderList(order)
                .amount(order.getTotalPrice())
                .transactionId(transactionId != null ? transactionId : UUID.randomUUID().toString())
                .status(PaymentStatus.PENDING)
                .build();
        Payment saved = paymentRepository.save(payment);
        return toDTO(saved);
    }

    public void confirmPayment(String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId).orElseThrow(() -> new RuntimeException("Payment not found"));
        payment.setStatus(PaymentStatus.PAID);
        paymentRepository.save(payment);
        OrderList order = payment.getOrderList();
        order.setStatus(OrderStatus.COMPLETED);
        orderListRepository.save(order);
    }

    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }
}