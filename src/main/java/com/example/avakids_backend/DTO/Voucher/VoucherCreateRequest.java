package com.example.avakids_backend.DTO.Voucher;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.*;

import com.example.avakids_backend.entity.Voucher.DiscountType;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;

// DTO cho việc tạo voucher
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherCreateRequest {

    @NotBlank(message = "VOUCHER_CODE_NULL")
    @Size(min = 3, max = 50, message = "VOUCHER_CODE_LENGTH_INVALID")
    @Pattern(regexp = "^[A-Z0-9_-]+$", message = "VOUCHER_CODE_FORMAT_INVALID")
    private String code;

    @NotBlank(message = "VOUCHER_NAME_NULL")
    @Size(max = 200, message = "VOUCHER_NAME_LENGTH_EXCEEDED")
    private String name;

    @NotNull(message = "DISCOUNT_TYPE_NULL")
    private DiscountType discountType;

    @NotNull(message = "DISCOUNT_VALUE_NULL")
    @DecimalMin(value = "0.01", message = "DISCOUNT_VALUE_INVALID")
    private BigDecimal discountValue;

    @DecimalMin(value = "0", message = "DISCOUNT_AMOUNT_INVALID")
    private BigDecimal maxDiscountAmount;

    @DecimalMin(value = "0", message = "MIN_ORDER_AMOUNT_INVALID")
    private BigDecimal minOrderAmount;

    @NotNull(message = "VOUCHER_TOTAL_QUANTITY_NULL")
    @Min(value = 1, message = "VOUCHER_TOTAL_QUANTITY_INVALID")
    private Integer totalQuantity;

    @Min(value = 1, message = "MSG_VOUCHER_USAGE_LIMIT_INVALID")
    private Integer usageLimitPerUser;

    @NotNull(message = "MSG_VOUCHER_START_TIME_NULL")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startAt;

    @NotNull(message = "MSG_VOUCHER_END_TIME_NULL")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endAt;
}
