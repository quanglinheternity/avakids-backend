package com.example.avakids_backend.repository.ProductVariant;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.avakids_backend.entity.ProductVariant;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long>, ProductVariantRepositoryCustom {

    List<ProductVariant> findByProductId(Long productId);

    Optional<ProductVariant> findByProductIdAndIsDefaultTrue(Long productId);

    Optional<ProductVariant> findByProductIdAndSku(Long productId, String sku);

    List<ProductVariant> findAllByProduct_Id(Long productId);

    Optional<ProductVariant> findFirstByProduct_IdOrderByIdAsc(Long productId);

    boolean existsByProductIdAndSku(Long productId, String sku);

    boolean existsByProductIdAndSkuAndIdNot(Long productId, String sku, Long variantId);

    @Modifying
    @Query("""
		UPDATE ProductVariant v
		SET v.isDefault = false
		WHERE v.product.id = :productId
	""")
    void resetDefaultVariant(@Param("productId") Long productId);
}
