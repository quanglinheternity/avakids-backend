package com.example.avakids_backend.repository.ProductVariantImage;

import java.util.Optional;

import com.example.avakids_backend.entity.ProductVariantImage;

public interface ProductVariantImageRepositoryCustom {
    Optional<ProductVariantImage> findPrimaryImageByVariantId(Long variantId);

    void resetPrimaryImagesByVariantId(Long variantId);

    void deleteByVariantId(Long variantId);

    Integer findMaxDisplayOrderByVariantId(Long variantId);
}
