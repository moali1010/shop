package com.example.tacoshop.service.impl;

import com.example.tacoshop.dto.request.DiscountRequest;
import com.example.tacoshop.dto.response.DiscountResponse;
import com.example.tacoshop.dto.response.PageResponse;
import com.example.tacoshop.entity.DiscountCode;
import com.example.tacoshop.entity.OrderEntity;
import com.example.tacoshop.exception.BusinessException;
import com.example.tacoshop.exception.ResourceNotFoundException;
import com.example.tacoshop.repository.DiscountCodeRepository;
import com.example.tacoshop.service.DiscountCodeService;
import com.example.tacoshop.service.discount.DiscountStrategy;
import com.example.tacoshop.service.discount.DiscountStrategyFactory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

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
        if (order.getTotalAmount() == null || order.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("INVALID_ORDER", "Order total amount must be set and positive");
        }
        DiscountStrategy strategy = discountStrategyFactory.getStrategy(discountCode.getType());
        long discountAmount = strategy.applyDiscount(order, discountCode);
        if (discountAmount > order.getTotalAmount().longValue()) {
            discountAmount = order.getTotalAmount().longValue();
        }
        logger.info("Applied discount {} for order {} using code {}",
                discountAmount, order.getId(), order.getDiscountCode());
        return discountAmount;
    }

    @Override
    @Transactional
    public DiscountResponse createDiscount(DiscountRequest request) {
        if (discountCodeRepository.findByCodeAndActiveIsTrue(request.code()).isPresent()) {
            throw new BusinessException("DISCOUNT_CODE_EXISTS", "Discount code already exists");
        }

        DiscountCode discountCode = DiscountCode.builder()
                .code(request.code())
                .type(request.type())
                .value(request.value())
                .maxAmount(request.maxAmount())
                .expiresAt(request.expiresAt())
                .build();

        DiscountCode savedDiscount = discountCodeRepository.save(discountCode);
        return mapToDiscountResponse(savedDiscount);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DiscountResponse> findAllDiscounts(Integer page, Integer size) {
        Page<DiscountCode> discountPage = discountCodeRepository.findAllByActiveIsTrue(
                PageRequest.of(page, size));

        List<DiscountResponse> discountResponses = discountPage.getContent().stream()
                .map(this::mapToDiscountResponse)
                .toList();

        return new PageResponse<>(discountResponses, discountPage.getTotalElements());
    }

    @Override
    @Transactional
    public DiscountResponse updateDiscount(Long id, DiscountRequest request) {
        DiscountCode discountCode = discountCodeRepository.findByIdAndActiveIsTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discount", "id", id));

        if (request.code() != null && !request.code().isBlank() &&
                !discountCode.getCode().equals(request.code())) {
            if (discountCodeRepository.findByCodeAndActiveIsTrue(request.code()).isPresent()) {
                throw new BusinessException("DISCOUNT_CODE_EXISTS", "Discount code already exists");
            }
            discountCode.setCode(request.code());
        }

        discountCode.setType(request.type());
        discountCode.setValue(request.value());
        discountCode.setMaxAmount(request.maxAmount());
        discountCode.setExpiresAt(request.expiresAt());

        DiscountCode updatedDiscount = discountCodeRepository.save(discountCode);
        return mapToDiscountResponse(updatedDiscount);
    }

    @Override
    @Transactional
    public void deactivateDiscount(Long id) {
        DiscountCode discountCode = discountCodeRepository.findByIdAndActiveIsTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discount", "id", id));

        discountCode.setActive(false);
        discountCodeRepository.save(discountCode);
    }

    private DiscountResponse mapToDiscountResponse(DiscountCode discountCode) {
        return new DiscountResponse(
                discountCode.getId(),
                discountCode.getCode(),
                discountCode.getType(),
                discountCode.getValue(),
                discountCode.getMaxAmount(),
                discountCode.getExpiresAt()
        );
    }
}
