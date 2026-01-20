package com.example.avakids_backend.DTO.ProductVariant;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantImageRequest {

    private String imageUrl;

    private Integer displayOrder;
}
