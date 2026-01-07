package com.example.avakids_backend.repository.ProductImage;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.avakids_backend.entity.ProductImage;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long>, ProductImageRepositoryCustom {

    List<ProductImage> findByProductIdOrderByDisplayOrderAsc(Long productId);

    long countByProductId(Long productId);
}
