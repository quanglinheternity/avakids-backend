package com.example.avakids_backend.DTO.ProductVariant;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductOptionValueResponseDTO {

    private Long id;

    private String value; // Đỏ, Xanh, M, L

    private Integer displayOrder; // Màu sắc, Size
}
