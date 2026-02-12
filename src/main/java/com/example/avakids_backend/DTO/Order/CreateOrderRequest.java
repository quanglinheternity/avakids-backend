package com.example.avakids_backend.DTO.Order;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import com.example.avakids_backend.enums.PaymentMethod;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderRequest {

    @NotNull(message = "MSG_ORDER_ITEMS_NULL")
    @Size(min = 1, message = "MSG_ORDER_ITEMS_EMPTY")
    @Valid
    private List<OrderItemRequest> orderItems;

    @NotNull(message = "MSG_SHIPPING_ADDRESS_NULL")
    @Valid
    private ShippingAddress shippingAddress;

    @Size(max = 1000, message = "MSG_CUSTOMER_NOTE_TOO_LONG")
    private String customerNote;

    private String couponCode;

    @NotNull(message = "MSG_PAYMENT_METHOD_NULL")
    private PaymentMethod paymentMethod;

    private String voucherCode;
    private boolean useUserVipPoint;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemRequest {

        private Long variantId;

        private Long productId;

        @NotNull(message = "MSG_QUANTITY_NULL")
        @Min(value = 1, message = "MSG_QUANTITY_INVALID")
        private Integer quantity;
    }
}
