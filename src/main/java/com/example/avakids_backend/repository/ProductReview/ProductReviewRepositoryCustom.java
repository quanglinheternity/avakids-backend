package com.example.avakids_backend.repository.ProductReview;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.avakids_backend.DTO.ProductReview.ProductReviewSearchRequest;
import com.example.avakids_backend.DTO.ProductReview.ProductReviewStats;
import com.example.avakids_backend.entity.ProductReview;

public interface ProductReviewRepositoryCustom {
    Page<ProductReview> searchReviews(ProductReviewSearchRequest searchRequest, Pageable pageable);

    ProductReviewStats getReviewStats(Long productId);
}
