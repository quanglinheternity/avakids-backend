package com.example.avakids_backend.DTO.ProductReview;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProductReviewSummaryResponse {
    private BigDecimal averageRating;
    private Integer totalReviews;
    private List<RatingDistribution> ratingDistribution;
    private Long verifiedPurchaseCount;
    private Long hasImagesCount;

    @Getter
    @Builder
    @Setter
    public static class RatingDistribution {
        private Integer rating;
        private Integer count;
        private Double percentage;
    }
}
