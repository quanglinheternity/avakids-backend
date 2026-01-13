package com.example.avakids_backend.DTO.ProductReview;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductReviewResponse {
    private Long id;
    private Long productId;
    private String productName;
    private Long userId;
    private String userName;
    private String userAvatar;
    private String orderCode;
    private Integer rating;
    private String content;
    private String imageUrl;
    private Boolean isVerifiedPurchase;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
