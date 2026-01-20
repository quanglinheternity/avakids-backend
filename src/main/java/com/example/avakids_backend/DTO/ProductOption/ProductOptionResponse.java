package com.example.avakids_backend.DTO.ProductOption;

import java.util.List;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductOptionResponse {

    private Long id;
    private String name;
    private List<ProductOptionValueResponse> values;
}
