package com.example.avakids_backend.DTO.Voucher;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VoucherUpdateRequest {

    @Size(max = 200, message = "VOUCHER_NAME_LENGTH_EXCEEDED")
    private String name;

    @DecimalMin(value = "0", message = "DISCOUNT_AMOUNT_INVALID")
    private BigDecimal maxDiscountAmount;

    @DecimalMin(value = "0", message = "MIN_ORDER_AMOUNT_INVALID")
    private BigDecimal minOrderAmount;

    @Min(value = 1, message = "VOUCHER_TOTAL_QUANTITY_INVALID")
    private Integer totalQuantity;

    @Min(value = 1, message = "MSG_VOUCHER_USAGE_LIMIT_INVALID")
    private Integer usageLimitPerUser;

    private Boolean isActive;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endAt;
}
