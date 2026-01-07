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

    private Long productId;

    private String productName;
    //    private String productImage;
    private BigDecimal productPrice;
    private Integer productStock;
    private Integer quantity;

    private BigDecimal subtotal;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
