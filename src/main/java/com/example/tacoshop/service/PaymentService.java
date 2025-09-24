package com.example.tacoshop.service;

import com.example.tacoshop.entity.OrderEntity;
import com.example.tacoshop.entity.PaymentTransaction;
import com.example.tacoshop.entity.User;
import com.example.tacoshop.entity.type.PaymentMethod;

import java.math.BigDecimal;

public interface PaymentService {

    PaymentTransaction initiateOrderPayment(User user, OrderEntity order, PaymentMethod method, BigDecimal amount);

    void confirmOrderPayment(Long transactionId, boolean success);

    void refundPayment(PaymentTransaction transaction, String reason);

    void refundPaymentsForOrder(OrderEntity order, String reason);

    boolean hasSuccessfulPayment(Long orderId);

    void initiateWalletCharge(User user, BigDecimal amount, PaymentMethod method);

    void repayCredit(User user, BigDecimal amount, PaymentMethod method);
}
