package com.example.avakids_backend.repository.ProductImage;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.avakids_backend.Entity.ProductImage;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByProductIdOrderByDisplayOrderAsc(Long productId);

    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.id = :productId AND pi.isPrimary = true")
    Optional<ProductImage> findPrimaryImageByProductId(@Param("productId") Long productId);

    long countByProductId(Long productId);

    @Modifying
    @Query("UPDATE ProductImage pi SET pi.isPrimary = false WHERE pi.product.id = :productId")
    void resetPrimaryImagesByProductId(@Param("productId") Long productId);

    @Modifying
    @Query("DELETE FROM ProductImage pi WHERE pi.product.id = :productId")
    void deleteByProductId(@Param("productId") Long productId);

    @Query("SELECT MAX(pi.displayOrder) FROM ProductImage pi WHERE pi.product.id = :productId")
    Integer findMaxDisplayOrderByProductId(@Param("productId") Long productId);
}
