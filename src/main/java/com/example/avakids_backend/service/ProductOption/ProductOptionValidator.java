package com.example.avakids_backend.service.ProductOption;

import org.springframework.stereotype.Component;

import com.example.avakids_backend.entity.Product;
import com.example.avakids_backend.entity.ProductOption;
import com.example.avakids_backend.exception.AppException;
import com.example.avakids_backend.exception.ErrorCode;
import com.example.avakids_backend.repository.ProductOption.ProductOptionRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductOptionValidator {
    private final ProductOptionRepository optionRepo;

    public ProductOption getById(Long optionId) {

        return optionRepo.findById(optionId).orElseThrow(() -> new AppException(ErrorCode.OPTION_NOT_FOUND));
    }

    public void validateAddOptionExists(Product product, String OptionName) {
        boolean optionExists =
                product.getOptions().stream().anyMatch(opt -> opt.getName().equalsIgnoreCase(OptionName));

        if (optionExists) {
            throw new AppException(ErrorCode.OPTION_NAME_ALREADY_EXISTS);
        }
    }

    public void validateUpdateOptionExists(Product product, String optionName, Long currentOptionId) {
        boolean optionExists = product.getOptions().stream()
                .anyMatch(opt ->
                        !opt.getId().equals(currentOptionId) && opt.getName().equalsIgnoreCase(optionName));

        if (optionExists) {
            throw new AppException(ErrorCode.OPTION_NAME_ALREADY_EXISTS);
        }
    }
}
