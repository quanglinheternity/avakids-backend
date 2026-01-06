package com.example.avakids_backend.DTO.ProductImage;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageResponse {

    private Long id;

    private Long productId;

    private String imageUrl;

    private Integer displayOrder;

    private Boolean isPrimary;
}
