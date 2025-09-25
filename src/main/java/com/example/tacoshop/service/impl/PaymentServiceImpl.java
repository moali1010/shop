package com.example.tacoshop.service.impl;

import com.example.tacoshop.config.ApplicationConstants;
import com.example.tacoshop.entity.OrderEntity;
import com.example.tacoshop.entity.PaymentTransaction;
import com.example.tacoshop.entity.User;
import com.example.tacoshop.entity.type.PaymentMethod;
import com.example.tacoshop.entity.type.PaymentPurpose;
import com.example.tacoshop.entity.type.PaymentStatus;
import com.example.tacoshop.exception.BusinessException;
import com.example.tacoshop.exception.InsufficientFundsException;
import com.example.tacoshop.repository.PaymentRepository;
import com.example.tacoshop.service.CreditService;
import com.example.tacoshop.service.PaymentService;
import com.example.tacoshop.service.WalletService;
import com.example.tacoshop.service.payment.PaymentProcessor;
import com.example.tacoshop.service.payment.PaymentProcessorFactory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentProcessorFactory paymentProcessorFactory;
    private final WalletService walletService;
    private final CreditService creditService;
    private final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Override
    @Transactional
    public PaymentTransaction initiateOrderPayment(User user, OrderEntity order, PaymentMethod method, BigDecimal amount) {
        if (paymentRepository.findByOrderIdAndStatus(order.getId(), PaymentStatus.SUCCESS).isPresent()) {
            throw new BusinessException("ORDER_ALREADY_PAID", "Order already paid");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("INVALID_AMOUNT", "Payment amount must be positive");
        }

        PaymentTransaction tx = createPaymentTransaction(user, order, method, amount);
        BigDecimal remainingAmount = amount;

        if (method == PaymentMethod.CREDIT) {
            BigDecimal availableCredit = creditService.getAvailableCredit(user);
            if (availableCredit.compareTo(remainingAmount) < 0) {
                creditService.debitCredit(user, availableCredit);
                remainingAmount = remainingAmount.subtract(availableCredit);
                PaymentProcessor bankProcessor = paymentProcessorFactory.getProcessor(PaymentMethod.ZARINPAL.name());
                bankProcessor.processPayment(user, remainingAmount, "Remaining payment for Order#" + order.getId());
                tx.setMethod(PaymentMethod.ZARINPAL);
            } else {
                creditService.debitCredit(user, remainingAmount);
                remainingAmount = BigDecimal.ZERO;
            }
        } else if (method == PaymentMethod.WALLET) {
            BigDecimal walletBalance = walletService.getBalance(user);
            if (walletBalance.compareTo(remainingAmount) < 0) {
                throw new InsufficientFundsException("Wallet", walletBalance, remainingAmount);
            }
            walletService.debitWallet(user, remainingAmount, "Payment for Order#" + order.getId());
            BigDecimal walletDiscount = remainingAmount.multiply(ApplicationConstants.WALLET_DISCOUNT_PERCENT);
            order.setDiscountAmount(order.getDiscountAmount().add(walletDiscount));
            remainingAmount = BigDecimal.ZERO;
        } else {
            PaymentProcessor processor = paymentProcessorFactory.getProcessor(method.name());
            processor.processPayment(user, remainingAmount, "Payment for Order#" + order.getId());
            remainingAmount = BigDecimal.ZERO;
        }

        if (remainingAmount.compareTo(BigDecimal.ZERO) > 0) {
            tx.setStatus(PaymentStatus.FAILED);
            logger.error("Payment failed for order {}: Remaining amount {}", order.getId(), remainingAmount);
            throw new BusinessException("PAYMENT_FAILED", "Payment processing failed");
        }

        tx.setStatus(PaymentStatus.SUCCESS);
        logger.info("Payment successful for order {} with method {}", order.getId(), method);
        return paymentRepository.save(tx);
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

    @Override
    public boolean hasSuccessfulPayment(Long orderId) {
        return paymentRepository.findByOrderIdAndStatus(orderId, PaymentStatus.SUCCESS).isPresent();
    }

    @Override
    @Transactional
    public void initiateWalletCharge(User user, BigDecimal amount, PaymentMethod method) {
        if (!method.name().equals(PaymentMethod.ZARINPAL.name()) && !method.name().equals(PaymentMethod.ASANPARDAKHT.name())) {
            throw new BusinessException("UNSUPPORTED_METHOD_FOR_CHARGE", "Only bank gateways for wallet charge");
        }
        PaymentTransaction tx = createPaymentTransaction(user, null, method, amount);
        tx.setPurpose(PaymentPurpose.WALLET_INCREMENT);
        PaymentProcessor processor = paymentProcessorFactory.getProcessor(method.name());
        processor.processPayment(user, amount, "Wallet charge");
        tx.setStatus(PaymentStatus.SUCCESS);
        walletService.creditWallet(user, amount, "Charged via " + method);
        paymentRepository.save(tx);
    }

    @Override
    @Transactional
    public void repayCredit(User user, BigDecimal amount, PaymentMethod method) {
        PaymentTransaction tx = createPaymentTransaction(user, null, method, amount);
        tx.setPurpose(PaymentPurpose.CREDIT_REPAYMENT);
        PaymentProcessor processor = paymentProcessorFactory.getProcessor(method.name());
        processor.processPayment(user, amount, "Credit repayment");
        tx.setStatus(PaymentStatus.SUCCESS);
        creditService.repayCredit(user, amount);
        paymentRepository.save(tx);
    }

    private PaymentTransaction createPaymentTransaction(User user, OrderEntity order, PaymentMethod method, BigDecimal amount) {
        return PaymentTransaction.builder()
                .user(user)
                .order(order)
                .amount(amount)
                .status(PaymentStatus.PENDING)
                .purpose(PaymentPurpose.ORDER_PAYMENT)
                .method(method)
                .providerRef(UUID.randomUUID().toString())
                .build();
    }

}
