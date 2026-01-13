package com.example.avakids_backend.DTO.Product;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchRequest {
    private String keyword;
    private Long categoryId;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Boolean isActive;
    private Boolean isFeatured;
    private Boolean inStock;
    private BigDecimal minRating;
    private String sortBy;
    private String sortDirection;
    private boolean adminSearch;
}
