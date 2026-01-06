package com.example.avakids_backend.DTO.Product;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductUpdateRequest {

    private String sku;

    private String name;

    private String slug;

    private Long categoryId;

    private String description;

    @DecimalMin(value = "0.0", message = "PRODUCT_PRICE_INVALID")
    private BigDecimal price;

    private BigDecimal salePrice;

    @Min(value = 0, message = "PRODUCT_STOCK_INVALID")
    private Integer stockQuantity;

    private Boolean isActive;
    private Boolean isFeatured;
}
