package com.example.avakids_backend.DTO.ProductVariant;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SelectVariantRequest {

    //    @NotEmpty(message = "OPTION_VALUE_REQUIRED")
    private List<Long> optionValueIds;
}
