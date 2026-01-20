package com.example.avakids_backend.DTO.Product;

import java.math.BigDecimal;

import jakarta.validation.constraints.*;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCreateRequest {

    @NotBlank(message = "PRODUCT_SKU_ALREADY_EXISTS")
    private String sku;

    @NotBlank(message = "PRODUCT_NAME_REQUIRED")
    private String name;

    @NotBlank(message = "PRODUCT_SLUG_REQUIRED")
    private String slug;

    @NotNull(message = "CATEGORY_ID_REQUIRED")
    private Long categoryId;

    private String description;

    @NotNull(message = "PRODUCT_PRICE_REQUIRED")
    @DecimalMin(value = "0.0", message = "PRODUCT_PRICE_INVALID")
    private BigDecimal price;

    @DecimalMin(value = "0.0", message = "PRODUCT_SALE_PRICE_INVALID")
    private BigDecimal salePrice;

    @Min(value = 0, message = "PRODUCT_STOCK_INVALID")
    private Integer totalStock;

    private Boolean isActive;
    private Boolean isFeatured;
}
