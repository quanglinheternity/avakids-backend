package com.example.avakids_backend.repository.Product;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.avakids_backend.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

    boolean existsBySku(String sku);

    boolean existsBySlug(String slug);

    boolean existsBySkuAndIdNot(String sku, Long id);

    boolean existsBySlugAndIdNot(String slug, Long id);
}
