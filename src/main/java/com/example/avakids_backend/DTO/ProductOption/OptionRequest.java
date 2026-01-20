package com.example.avakids_backend.DTO.ProductOption;

import java.util.List;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptionRequest {
    private Long productId;
    private String name;
    private List<OptionValueRequest> optionValues;
}
