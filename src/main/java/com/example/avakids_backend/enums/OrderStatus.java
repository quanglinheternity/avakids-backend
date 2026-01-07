package com.example.avakids_backend.enums;

import java.util.Set;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING("PENDING", "Chờ xác nhận"),
    CONFIRMED("CONFIRMED", "Đã xác nhận"),
    PROCESSING("PROCESSING", "Đang xử lý"),
    SHIPPED("SHIPPED", "Đã giao cho đơn vị vận chuyển"),
    DELIVERED("DELIVERED", "Đã giao hàng"),
    CANCELLED("CANCELLED", "Đã hủy"),
    REFUNDED("REFUNDED", "Đã hoàn tiền");

    private final String code;
    private final String description;
    private Set<OrderStatus> nextStatuses;

    OrderStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    static {
        PENDING.nextStatuses = Set.of(CONFIRMED, CANCELLED);
        CONFIRMED.nextStatuses = Set.of(PROCESSING, CANCELLED);
        PROCESSING.nextStatuses = Set.of(SHIPPED, CANCELLED);
        SHIPPED.nextStatuses = Set.of(DELIVERED);
        DELIVERED.nextStatuses = Set.of();
        CANCELLED.nextStatuses = Set.of();
        REFUNDED.nextStatuses = Set.of();
    }

    public boolean canChangeTo(OrderStatus newStatus) {
        return nextStatuses.contains(newStatus);
    }
}
