package com.example.tacoshop.service.payment;

import com.example.tacoshop.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentProcessorFactory {

    private final List<PaymentProcessor> processors;

    public PaymentProcessor getProcessor(String paymentMethod) {
        return processors.stream()
                .filter(processor -> processor.canProcess(paymentMethod))
                .findFirst()
                .orElseThrow(() ->
                        new BusinessException("UNSUPPORTED_METHOD", "Unsupported payment method: " + paymentMethod));
    }

}
