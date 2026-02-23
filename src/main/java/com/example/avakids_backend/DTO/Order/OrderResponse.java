package com.example.avakids_backend.DTO.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.avakids_backend.enums.OrderStatus;
import com.example.avakids_backend.enums.PaymentMethod;
import com.example.avakids_backend.enums.PaymentStatus;

import lombok.Data;

@Data
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private String statusName;
    private String paymentStatus;
    private String paymentMethod;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;

    public OrderResponse(
            Long id,
            String orderNumber,
            OrderStatus status,
            PaymentStatus paymentStatus,
            PaymentMethod paymentMethod,
            BigDecimal totalAmount,
            LocalDateTime createdAt) {

        this.id = id;
        this.orderNumber = orderNumber;
        this.statusName = status != null ? status.getDescription() : null;
        this.paymentMethod = paymentMethod.getDescription();
        this.paymentStatus = paymentStatus.getDescription();
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
    }
}
