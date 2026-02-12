package com.example.avakids_backend.repository.ProductOption;

import java.util.List;

import com.example.avakids_backend.entity.ProductOption;

public interface ProductOptionRepositoryCustom {
    List<ProductOption> findOptionsHasVariantByProductId(Long productId);
}
