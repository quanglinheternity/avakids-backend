package com.example.avakids_backend.service.ProductVariant;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.avakids_backend.entity.ProductOptionValue;
import com.example.avakids_backend.entity.ProductVariant;
import com.example.avakids_backend.exception.AppException;
import com.example.avakids_backend.exception.ErrorCode;
import com.example.avakids_backend.repository.ProductVariant.ProductVariantRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductVariantValidator {
    private final ProductVariantRepository variantRepository;

    public ProductVariant getVariantById(Long variantId) {
        return variantRepository.findById(variantId).orElseThrow(() -> new AppException(ErrorCode.VARIANT_NOT_FOUND));
    }

    public void existsVariantWithExactOptions(Long productId, List<Long> optionValueIds) {
        if (variantRepository.existsVariantWithExactOptions(productId, optionValueIds)) {
            throw new AppException(ErrorCode.VARIANT_ALREADY_EXISTS);
        }
    }

    public void validateNoDuplicateVariantForUpdate(Long productId, Long variantId, List<Long> optionValueIds) {
        if (variantRepository.existsOtherVariantWithExactOptions(productId, variantId, optionValueIds)) {
            throw new AppException(ErrorCode.VARIANT_ALREADY_EXISTS);
        }
    }

    public void validateVariantBelongsToProduct(ProductVariant variant, Long productId) {
        if (!variant.getProduct().getId().equals(productId)) {
            throw new AppException(ErrorCode.VARIANT_NOT_BELONG_TO_PRODUCT);
        }
    }

    public void validateNoDuplicateOption(List<ProductOptionValue> values) {
        long distinctOptionCount =
                values.stream().map(v -> v.getOption().getId()).distinct().count();

        if (distinctOptionCount != values.size()) {
            throw new AppException(ErrorCode.DUPLICATE_OPTION_TYPE);
        }
    }
}
