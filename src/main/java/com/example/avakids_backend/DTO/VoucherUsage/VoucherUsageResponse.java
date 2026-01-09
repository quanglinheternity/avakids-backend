package com.example.avakids_backend.DTO.VoucherUsage;


import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class VoucherUsageResponse {

    private Long id;

    private Long voucherId;
    private String voucherCode;
    private String voucherName;

    private Long userId;
    private String userFullName;
    private String userEmail;

    private Long orderId;

    private BigDecimal orderAmount;
    private BigDecimal discountAmount;

    private LocalDateTime usedAt;
}
