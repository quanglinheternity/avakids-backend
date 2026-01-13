package com.example.avakids_backend.repository.Product;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.avakids_backend.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsBySku(String sku);

    boolean existsBySlug(String slug);

    boolean existsBySkuAndIdNot(String sku, Long id);

    boolean existsBySlugAndIdNot(String slug, Long id);

    @Modifying
    @Query("UPDATE Product p " + "SET p.avgRating = :avgRating, "
            + "    p.reviewCount = :reviewCount, "
            + "    p.updatedAt = CURRENT_TIMESTAMP "
            + "WHERE p.id = :productId")
    void updateProductRating(
            @Param("productId") Long productId,
            @Param("avgRating") BigDecimal avgRating,
            @Param("reviewCount") Integer reviewCount);
}
