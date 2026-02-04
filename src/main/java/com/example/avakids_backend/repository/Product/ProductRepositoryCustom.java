package com.example.avakids_backend.repository.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.avakids_backend.DTO.Product.ProductResponse;
import com.example.avakids_backend.DTO.Product.ProductSearchRequest;
import com.example.avakids_backend.entity.Product;

public interface ProductRepositoryCustom {
    Page<ProductResponse> searchProducts(ProductSearchRequest criteria, Pageable pageable);

    List<ProductResponse> findFeaturedProducts(int limit);

    List<ProductResponse> findBestSellingProducts(int limit);

    List<ProductResponse> findNewProducts(int limit);

    List<ProductResponse> findRelatedProducts(Long productId, Long categoryId, int limit);

    Optional<Product> findByIdWithImages(Long id);

    Optional<Product> findBySlugWithDetails(String slug);

    void updateProductRating(Long productId, BigDecimal avgRating, Integer reviewCount);

    List<Product> findByCategoryAndExcludeIds(Long categoryId, Set<Long> excludedIds, int minStock, int limit);

    List<Product> findByCategoryIdsExcludeIds(Set<Long> categoryIds, Set<Long> excludedIds, int minStock, int limit);

    List<Product> findPopularExcludeIds(Set<Long> excludedIds, int minStock, int limit);

    List<Product> findAnyInStockExcludeIds(Set<Long> excludedIds, int limit);
}
