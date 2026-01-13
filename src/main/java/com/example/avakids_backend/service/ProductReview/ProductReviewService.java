package com.example.avakids_backend.service.ProductReview;

import com.example.avakids_backend.DTO.ProductReview.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductReviewService {
    ProductReviewResponse createReview(ProductReviewCreateRequest request);

    ProductReviewResponse updateReview(Long reviewId, ProductReviewUpdateRequest request);

    void deleteReview(Long reviewId);

    Page<ProductReviewResponse> searchReviews(ProductReviewSearchRequest searchRequest, Pageable pageable);
    ProductReviewSummaryResponse getProductReviewSummary(Long productId);
}
