package com.example.avakids_backend.repository.ProductVariantImage;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.avakids_backend.entity.ProductVariantImage;

@Repository
public interface ProductVariantImageRepository
        extends JpaRepository<ProductVariantImage, Long>, ProductVariantImageRepositoryCustom {

    List<ProductVariantImage> findByVariantIdOrderByDisplayOrderAsc(Long variantId);

    long countByVariantId(Long variantId);
}
