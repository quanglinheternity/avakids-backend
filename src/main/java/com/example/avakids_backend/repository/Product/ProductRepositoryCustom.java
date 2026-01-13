package com.example.avakids_backend.repository.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.avakids_backend.DTO.Product.ProductSearchRequest;
import com.example.avakids_backend.entity.Product;

public interface ProductRepositoryCustom {
    Page<Product> searchProducts(ProductSearchRequest criteria, Pageable pageable);

    List<Product> findFeaturedProducts(int limit);

    List<Product> findBestSellingProducts(int limit);

    List<Product> findNewProducts(int limit);

    List<Product> findRelatedProducts(Long productId, Long categoryId, int limit);

    Optional<Product> findByIdWithImages(Long id);

    Optional<Product> findBySlugWithDetails(String slug);

    void updateProductRating(Long productId, BigDecimal avgRating, Integer reviewCount);
}
