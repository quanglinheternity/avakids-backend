package com.example.avakids_backend.DTO.Voucher;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VoucherValidationResponse {
    private Boolean isValid;
    private String message;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private VoucherResponse voucher;
}
