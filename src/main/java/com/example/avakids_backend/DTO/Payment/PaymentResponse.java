package com.example.avakids_backend.DTO.Payment;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private boolean success;
    private String message;
    private String transactionId;
    private BigDecimal amount;
    private String bankCode;
    private String paymentDate;
}
