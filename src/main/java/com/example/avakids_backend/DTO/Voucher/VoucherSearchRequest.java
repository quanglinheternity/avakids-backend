package com.example.avakids_backend.DTO.Voucher;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoucherSearchRequest {
    private String keyword;
    private String code;

    private String name;

    private Boolean isActive;

    private LocalDate fromDate;

    private LocalDate toDate;
}
