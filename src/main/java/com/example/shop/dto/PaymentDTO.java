package com.example.shop.dto;

import com.example.shop.model.type.PaymentStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {

    private Long id;

    private Long orderListId;

    private Double amount;

    private PaymentStatus status;

    private LocalDateTime paymentTime;

    private String transactionId;
}
