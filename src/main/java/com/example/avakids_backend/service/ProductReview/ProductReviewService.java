package com.example.avakids_backend.service.ProductReview;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.example.avakids_backend.DTO.ProductReview.*;

public interface ProductReviewService {
    ProductReviewResponse createReview(ProductReviewCreateRequest request, MultipartFile file);

    ProductReviewResponse updateReview(Long reviewId, ProductReviewUpdateRequest request, MultipartFile file);

    void deleteReview(Long reviewId);

    Page<ProductReviewResponse> searchReviews(ProductReviewSearchRequest searchRequest, Pageable pageable);

    ProductReviewSummaryResponse getProductReviewSummary(Long productId);
}
