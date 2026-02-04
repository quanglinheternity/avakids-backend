package com.example.avakids_backend.repository.ProductVariant;

import java.util.List;
import java.util.Optional;

import com.example.avakids_backend.DTO.ProductVariant.ProductAggregateResult;
import com.example.avakids_backend.DTO.ProductVariant.ProductVariantResponse;
import com.example.avakids_backend.entity.ProductVariant;

public interface ProductVariantRepositoryCustom {
    Optional<ProductVariant> findExactVariant(Long productId, List<Long> optionValueIds);

    Optional<ProductVariant> findVariantBySku(Long productId, String sku);

    boolean existsVariantWithExactOptions(Long productId, List<Long> optionValueIds);

    boolean existsOtherVariantWithExactOptions(Long productId, Long currentVariantId, List<Long> optionValueIds);

    ProductAggregateResult aggregateByProductId(Long productId);

    Integer sumStockByProductId(Long productId);

    void resetDefaultVariant(Long productId);

    List<ProductVariantResponse> getVariantsByProductId(Long productId);
}
