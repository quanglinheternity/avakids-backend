package com.example.avakids_backend.DTO.ProductReview;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductReviewSearchRequest {

    private Long productId;

    private Long userId;

    private Long orderId;

    private Integer rating;

    private Integer minRating;

    private Integer maxRating;

    private Boolean isVerifiedPurchase;

    private Boolean hasImages;

    private String keyword;

    private LocalDateTime startDate;

    private LocalDateTime endDate;
}
