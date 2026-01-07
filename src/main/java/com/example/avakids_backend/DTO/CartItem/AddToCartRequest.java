package com.example.avakids_backend.DTO.CartItem;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddToCartRequest {

    @NotNull(message = "PRODUCT_NAME_REQUIRED")
    private Long productId;

    @NotNull(message = "PRODUCT_QUANTITY_REQUIRED")
    @Min(value = 1, message = "PRODUCT_QUANTITY_MIN")
    private Integer quantity;
}
