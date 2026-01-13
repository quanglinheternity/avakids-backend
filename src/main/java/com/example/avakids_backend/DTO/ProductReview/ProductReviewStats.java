package com.example.avakids_backend.DTO.ProductReview;

import java.math.BigDecimal;
import java.util.Map;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductReviewStats {
    private BigDecimal averageRating;
    private Long totalReviews;
    private Long verifiedPurchaseCount;
    private Long hasImagesCount;
    private Map<Integer, Long> ratingDistribution;
}
