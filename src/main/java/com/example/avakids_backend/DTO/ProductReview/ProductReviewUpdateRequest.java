package com.example.avakids_backend.DTO.ProductReview;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductReviewUpdateRequest {

    @NotNull(message = "RATING_NOT_NULL")
    @Min(value = 1, message = "RATING_MIN")
    @Max(value = 5, message = "RATING_MAX")
    private Integer rating;

    @Size(max = 2000, message = "CONTENT_SIZE")
    private String content;

    @Size(max = 500, message = "IMAGE_URL_SIZE")
    private String imageUrl;
}
