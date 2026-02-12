package com.example.avakids_backend.DTO.CartItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponse {
    private Long id;

    private Long variantId;

    private String variantName;
    private String productImage;
    private BigDecimal variantPrice;
    private Integer variantStock;
    private Integer quantity;

    private BigDecimal subtotal;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
