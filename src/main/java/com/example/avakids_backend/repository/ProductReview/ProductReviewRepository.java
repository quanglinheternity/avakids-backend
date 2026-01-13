package com.example.avakids_backend.repository.ProductReview;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.avakids_backend.entity.ProductReview;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Long>, ProductReviewRepositoryCustom {

    boolean existsByUserIdAndProductIdAndOrderId(Long userId, Long productId, Long orderId);

    List<ProductReview> findByProductId(Long productId);
}
