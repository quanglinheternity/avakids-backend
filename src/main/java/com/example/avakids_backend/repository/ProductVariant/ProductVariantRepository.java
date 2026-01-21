package com.example.avakids_backend.repository.ProductVariant;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.avakids_backend.entity.ProductVariant;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long>, ProductVariantRepositoryCustom {

    List<ProductVariant> findAllByProduct_Id(Long productId);

    Optional<ProductVariant> findFirstByProduct_IdOrderByIdAsc(Long productId);

    boolean existsByProductIdAndSku(Long productId, String sku);
}
