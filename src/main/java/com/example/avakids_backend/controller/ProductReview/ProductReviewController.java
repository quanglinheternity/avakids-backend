package com.example.avakids_backend.controller.ProductReview;

import com.example.avakids_backend.util.language.I18n;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.avakids_backend.DTO.ApiResponse;
import com.example.avakids_backend.DTO.ProductReview.*;
import com.example.avakids_backend.service.ProductReview.ProductReviewService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Tag(name = "Product Review Management", description = "APIs for managing customer reviews and ratings for products")
public class ProductReviewController {
    private final ProductReviewService productReviewService;
    private final I18n i18n;
    

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Create a new product review",
            description = "Create a new review for a product with rating, comment, and optional image attachment")
    public ResponseEntity<ApiResponse<ProductReviewResponse>> createReview(
            @Valid @RequestPart("data") ProductReviewCreateRequest request, @RequestPart("file") MultipartFile file) {

        ProductReviewResponse response = productReviewService.createReview(request, file);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<ProductReviewResponse>builder()
                        .message(i18n.t("review.create.success"))
                        .data(response)
                        .build());
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping(value = "/{reviewId}/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Update an existing product review",
            description =
                    "Update an existing review including rating, comment, and optionally replace the attached image")
    public ResponseEntity<ApiResponse<ProductReviewResponse>> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestPart("data") ProductReviewUpdateRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        ProductReviewResponse response = productReviewService.updateReview(reviewId, request, file);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.<ProductReviewResponse>builder()
                        .message(i18n.t("review.update.success"))
                        .data(response)
                        .build());
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @DeleteMapping("/{reviewId}/delete")
    @Operation(summary = "Delete a product review", description = "Delete a specific review by ID")
    public ResponseEntity<ApiResponse<ProductReviewResponse>> deleteReview(@PathVariable Long reviewId) {

        productReviewService.deleteReview(reviewId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.<ProductReviewResponse>builder()
                        .message(i18n.t("review.delete.success"))
                        .build());
    }

    @GetMapping("/{reviewId}/summary")
    @Operation(
            summary = "Get product review summary",
            description =
                    "Get review statistics for a specific product including average rating and rating distribution")
    public ResponseEntity<ApiResponse<ProductReviewSummaryResponse>> getProductReviewSummary(
            @PathVariable Long reviewId) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.<ProductReviewSummaryResponse>builder()
                        .message(i18n.t("review.summary.success"))
                        .data(productReviewService.getProductReviewSummary(reviewId))
                        .build());
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/search")
    @Operation(
            summary = "Search product reviews",
            description = "Search and filter product reviews with pagination and various criteria")
    public ResponseEntity<ApiResponse<Page<ProductReviewResponse>>> searchReviews(
            ProductReviewSearchRequest searchRequest,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok()
                .body(ApiResponse.<Page<ProductReviewResponse>>builder()
                        .message(i18n.t("review.search.success"))
                        .data(productReviewService.searchReviews(searchRequest, pageable))
                        .build());
    }

    @Operation(summary = "Get reviews by product ID", description = "Get all reviews for a specific product")
    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<Page<ProductReviewResponse>>> getReviewsByProductId(
            @PathVariable Long productId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok()
                .body(ApiResponse.<Page<ProductReviewResponse>>builder()
                        .message(i18n.t("review.get.by.product.success"))
                        .data(productReviewService.getReviewsByProductId(productId, pageable))
                        .build());
    }
}
