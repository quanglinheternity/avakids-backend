package com.example.avakids_backend.DTO.ProductImage;

import jakarta.validation.constraints.NotNull;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    private Integer displayOrder;

    private Boolean isPrimary;
}
