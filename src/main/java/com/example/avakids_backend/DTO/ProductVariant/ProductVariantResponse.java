package com.example.avakids_backend.DTO.ProductVariant;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantResponse {

    private Long id;

    private String sku;

    private String variantName;

    private BigDecimal price;

    private BigDecimal salePrice;

    private Integer stockQuantity;

    private Integer soldCount;

    private BigDecimal weight;

    private String dimensions;

    private String barcode;

    private Boolean isDefault;

    private List<ProductOptionValueResponseDTO> optionValues;

    //    private List<ProductVariantImageResponse> images;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
