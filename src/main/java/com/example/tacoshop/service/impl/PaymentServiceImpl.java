package com.example.tacoshop.service.impl;

import com.example.tacoshop.entity.OrderEntity;
import com.example.tacoshop.entity.PaymentTransaction;
import com.example.tacoshop.entity.User;
import com.example.tacoshop.entity.type.PaymentMethod;
import com.example.tacoshop.entity.type.PaymentPurpose;
import com.example.tacoshop.entity.type.PaymentStatus;
import com.example.tacoshop.exception.BusinessException;
import com.example.tacoshop.exception.ResourceNotFoundException;
import com.example.tacoshop.repository.PaymentRepository;
import com.example.tacoshop.service.PaymentService;
import com.example.tacoshop.service.payment.PaymentProcessor;
import com.example.tacoshop.service.payment.PaymentProcessorFactory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentProcessorFactory paymentProcessorFactory;
    private final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Override
    @Transactional
    public PaymentTransaction initiateOrderPayment(User user, OrderEntity order, PaymentMethod method, BigDecimal amount) {
        paymentRepository.findByOrderIdAndStatus(order.getId(), PaymentStatus.SUCCESS)
                .ifPresent(tx -> {
                    throw new BusinessException("ORDER_ALREADY_PAID", "Order already paid");
                });
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("INVALID_AMOUNT", "Payment amount must be positive");
        }
        PaymentTransaction tx = createPaymentTransaction(user, order, method, amount);
        try {
            PaymentProcessor processor = paymentProcessorFactory.getProcessor(method.name());
            processor.processPayment(user, amount, "Payment for Order#" + order.getId());
            tx.setStatus(PaymentStatus.SUCCESS);
            logger.info("Payment successful for order {} with method {}", order.getId(), method);
        } catch (Exception e) {
            tx.setStatus(PaymentStatus.FAILED);
            logger.error("Payment failed for order {}: {}", order.getId(), e.getMessage());
            throw new BusinessException("PAYMENT_FAILED", "Payment processing failed: " + e.getMessage());
        }
        return paymentRepository.save(tx);
    }

    @Override
    @Transactional
    public void confirmOrderPayment(Long transactionId, boolean success) {
        PaymentTransaction tx = paymentRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", transactionId));
        if (tx.getStatus() != PaymentStatus.PENDING) {
            return;
        }
        if (success) {
            tx.setStatus(PaymentStatus.SUCCESS);
        } else {
            tx.setStatus(PaymentStatus.FAILED);
            refundPayment(tx, "Payment confirmation failed");
        }
        paymentRepository.save(tx);
    }

    @Override
    @Transactional
    public void refundPayment(PaymentTransaction transaction, String reason) {
        if (transaction.getStatus() != PaymentStatus.SUCCESS) {
            logger.warn("Cannot refund non-successful transaction {}", transaction.getId());
            return;
        }
        logger.info("Refunding transaction {} with reason: {}", transaction.getId(), reason);
        try {
            PaymentProcessor processor = paymentProcessorFactory.getProcessor(transaction.getMethod().name());
            processor.processRefund(transaction.getUser(), transaction.getAmount(), "Refund for " + reason + " - Transaction#" + transaction.getId());
            transaction.setStatus(PaymentStatus.REVERSED);
        } catch (Exception e) {
            logger.error("Refund failed for transaction {}: {}", transaction.getId(), e.getMessage());
            throw new BusinessException("REFUND_FAILED", "Refund processing failed: " + e.getMessage());
        }
        paymentRepository.save(transaction);
    }

    @Override
    @Transactional
    public void refundPaymentsForOrder(OrderEntity order, String reason) {
        List<PaymentTransaction> transactions = paymentRepository.findByOrderId(order.getId());
        if (transactions.isEmpty()) {
            logger.info("No payments to refund for order {}", order.getId());
            return;
        }
        for (PaymentTransaction tx : transactions) {
            refundPayment(tx, reason + " - Order#" + order.getId());
        }
        logger.info("All payments refunded for order {}", order.getId());
    }

    private PaymentTransaction createPaymentTransaction(User user, OrderEntity order, PaymentMethod method, BigDecimal amount) {
        return PaymentTransaction.builder()
                .user(user)
                .order(order)
                .amount(amount)
                .status(PaymentStatus.PENDING)
                .purpose(PaymentPurpose.ORDER_PAYMENT)
                .method(method)
                .createdAt(OffsetDateTime.now())
                .providerRef(UUID.randomUUID().toString())
                .build();
    }
}
