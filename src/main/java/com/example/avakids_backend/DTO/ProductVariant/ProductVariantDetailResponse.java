package com.example.avakids_backend.DTO.ProductVariant;

import java.math.BigDecimal;
import java.util.List;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantDetailResponse {

    private Long id;
    private String sku;
    private String variantName;
    private BigDecimal price;
    private BigDecimal salePrice;
    private Integer stockQuantity;

    private Integer soldCount;
    private String barcode;
    private Boolean isDefault;
    private BigDecimal avgRating;
    private Integer reviewCount;
    private List<ProductOptionValueResponseDTO> optionValues;
}
