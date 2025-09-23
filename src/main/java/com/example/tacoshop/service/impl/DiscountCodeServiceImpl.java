package com.example.tacoshop.service.impl;

import com.example.tacoshop.entity.DiscountCode;
import com.example.tacoshop.entity.OrderEntity;
import com.example.tacoshop.exception.BusinessException;
import com.example.tacoshop.repository.DiscountCodeRepository;
import com.example.tacoshop.service.DiscountCodeService;
import com.example.tacoshop.service.discount.DiscountStrategy;
import com.example.tacoshop.service.discount.DiscountStrategyFactory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class DiscountCodeServiceImpl implements DiscountCodeService {

    private final DiscountStrategyFactory discountStrategyFactory;
    private final DiscountCodeRepository discountCodeRepository;
    private final Logger logger = LoggerFactory.getLogger(DiscountCodeServiceImpl.class);

    @Override
    public long calculateDiscountCode(OrderEntity order) {
        if (order.getDiscountCode() == null || order.getDiscountCode().isBlank()) {
            return 0L;
        }
        DiscountCode discountCode = discountCodeRepository.findByCodeAndActiveIsTrueAndExpiresAtAfter(
                        order.getDiscountCode(), OffsetDateTime.now())
                .orElseThrow(() -> new BusinessException("INVALID_DISCOUNT_CODE",
                        "Discount code is invalid or expired: " + order.getDiscountCode()));
        if (order.getTotalAmount() == null || order.getTotalAmount() <= 0) {
            throw new BusinessException("INVALID_ORDER", "Order total amount must be set and positive");
        }
        DiscountStrategy strategy = discountStrategyFactory.getStrategy(discountCode.getType());
        long discountAmount = strategy.applyDiscount(order, discountCode);
        if (discountAmount > order.getTotalAmount()) {
            discountAmount = order.getTotalAmount();
        }
        logger.info("Applied discount {} for order {} using code {}",
                discountAmount, order.getId(), order.getDiscountCode());
        return discountAmount;
    }

}
