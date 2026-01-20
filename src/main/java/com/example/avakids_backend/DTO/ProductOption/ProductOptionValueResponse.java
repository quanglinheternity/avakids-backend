package com.example.avakids_backend.DTO.ProductOption;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductOptionValueResponse {

    private Long id;
    private String value;
    private Integer displayOrder;
}
