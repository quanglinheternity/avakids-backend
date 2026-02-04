package com.example.avakids_backend.repository.ProductImage;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.avakids_backend.entity.ProductImage;

public interface ProductImageRepositoryCustom {
    Optional<ProductImage> findPrimaryImageByProductId(Long productId);

    Map<Long, String> loadPrimaryImages(List<Long> productIds);

    void resetPrimaryImagesByProductId(Long productId);

    void deleteByProductId(Long productId);

    Integer findMaxDisplayOrderByProductId(Long productId);
}
