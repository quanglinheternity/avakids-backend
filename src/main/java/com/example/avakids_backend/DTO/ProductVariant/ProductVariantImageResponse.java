package com.example.avakids_backend.DTO.ProductVariant;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantImageResponse {
    private String imageUrl;

    private Integer displayOrder;
}
