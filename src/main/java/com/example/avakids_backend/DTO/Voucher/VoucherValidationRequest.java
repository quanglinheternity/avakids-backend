package com.example.avakids_backend.DTO.Voucher;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherValidationRequest {
    @NotBlank(message = "VOUCHER_CODE_NULL")
    private String code;

    @NotNull(message = "MIN_ORDER_AMOUNT_NULL")
    @DecimalMin(value = "0.01", message = "MIN_ORDER_AMOUNT_INVALID")
    private BigDecimal orderAmount;
}
