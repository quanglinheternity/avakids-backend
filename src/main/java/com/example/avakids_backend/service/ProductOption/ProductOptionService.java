package com.example.avakids_backend.service.ProductOption;

import java.util.List;

import com.example.avakids_backend.DTO.ProductOption.OptionRequest;
import com.example.avakids_backend.DTO.ProductOption.OptionValueRequest;
import com.example.avakids_backend.DTO.ProductOption.ProductOptionResponse;

public interface ProductOptionService {
    ProductOptionResponse addNewOption(OptionRequest request);

    ProductOptionResponse addValuesToOption(Long optionId, List<OptionValueRequest> optionValues);

    ProductOptionResponse updateOption(Long optionId, OptionRequest request);

    List<ProductOptionResponse> getOptionByProduct(Long productId);
}
