package com.example.avakids_backend.DTO.ProductVariant;

import java.math.BigDecimal;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductAggregateResult {

    private Long variantCount;
    private Integer totalStock;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
}
