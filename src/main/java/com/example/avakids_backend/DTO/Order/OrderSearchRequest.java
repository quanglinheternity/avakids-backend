package com.example.avakids_backend.DTO.Order;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.example.avakids_backend.enums.OrderStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderSearchRequest {
    private Long userId;

    private OrderStatus status;

    private String orderCode;

    private String keyword;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fromDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate toDate;
}
