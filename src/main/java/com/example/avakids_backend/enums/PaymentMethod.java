package com.example.avakids_backend.enums;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    COD("Thanh toán khi nhận hàng"),
    BANKING("Chuyển khoản ngân hàng"),
    EWALLET("Ví điện tử");

    private final String description;

    PaymentMethod(String description) {
        this.description = description;
    }
}
