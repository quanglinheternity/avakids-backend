package com.example.avakids_backend.DTO.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

    private Long id;
    private String sku;
    private String name;
    private String slug;

    private String imageUlr;

    private Long categoryId;
    private String categoryName;

    private String description;
    private BigDecimal price;
    private BigDecimal salePrice;
    private Boolean hasVariants;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    private Integer totalStock;

    private Boolean isActive;
    private Boolean isFeatured;

    private BigDecimal avgRating;
    private Integer reviewCount;
    private Integer soldCount;
    private Boolean isFavorite;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
