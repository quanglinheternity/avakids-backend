package com.example.avakids_backend.DTO.ProductReview;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductReviewCreateRequest {

    @NotNull(message = "PRODUCT_ID_NULL")
    private Long productId;

    @NotNull(message = "ORDER_ID_NULL")
    private Long orderId;

    @NotNull(message = "RATING_NOT_NULL")
    @Min(value = 1, message = "RATING_MIN")
    @Max(value = 5, message = "RATING_MAX")
    private Integer rating;

    @Size(max = 2000, message = "CONTENT_SIZE")
    private String content;
}
