package com.example.avakids_backend.DTO.Voucher;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.avakids_backend.entity.Voucher;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherResponse {
    private Long id;
    private String code;
    private String name;
    private Voucher.DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal maxDiscountAmount;
    private BigDecimal minOrderAmount;
    private Integer totalQuantity;
    private Integer usedQuantity;
    private Integer remainingQuantity;
    private Integer usageLimitPerUser;
    private Boolean isActive;
    private Boolean isAvailable;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
}
