package com.example.avakids_backend.repository.ProductVariant;

import java.util.List;
import java.util.Optional;

import com.example.avakids_backend.DTO.ProductVariant.ProductAggregateResult;
import com.example.avakids_backend.DTO.ProductVariant.ProductVariantDetailResponse;
import com.example.avakids_backend.DTO.ProductVariant.ProductVariantResponse;

public interface ProductVariantRepositoryCustom {
    Optional<ProductVariantDetailResponse> findExactVariant(Long productId, List<Long> optionValueIds);

    Optional<ProductVariantDetailResponse> findVariantBySku(Long productId, String sku);

    boolean existsVariantWithExactOptions(Long productId, List<Long> optionValueIds);

    boolean existsOtherVariantWithExactOptions(Long productId, Long currentVariantId, List<Long> optionValueIds);

    ProductAggregateResult aggregateByProductId(Long productId);

    Integer sumStockByProductId(Long productId);

    void resetDefaultVariant(Long productId);

    List<ProductVariantResponse> getVariantsByProductId(Long productId);
}
