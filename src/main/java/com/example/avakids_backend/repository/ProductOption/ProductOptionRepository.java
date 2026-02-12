package com.example.avakids_backend.repository.ProductOption;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.avakids_backend.entity.ProductOption;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Long>, ProductOptionRepositoryCustom {

    List<ProductOption> findByProductId(Long productId);
}
