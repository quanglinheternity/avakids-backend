package com.example.avakids_backend.controller.ProductReview;

import com.example.avakids_backend.DTO.ProductReview.*;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.avakids_backend.DTO.ApiResponse;
import com.example.avakids_backend.service.ProductReview.ProductReviewService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Tag(name = "Product Reviews", description = "APIs for managing product reviews")
public class ProductReviewController {
    private final ProductReviewService productReviewService;

    @PostMapping("/create")
    @Operation(summary = "Create a new product review")
    public ResponseEntity<ApiResponse<ProductReviewResponse>> createReview(
            @Valid @RequestBody ProductReviewCreateRequest request) {

        ProductReviewResponse response = productReviewService.createReview(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<ProductReviewResponse>builder()
                        .message("Tạo đánh giá sản phẩm thành công")
                        .data(response)
                        .build());
    }

    @PutMapping("/{reviewId}/update")
    @Operation(summary = "update a new product review")
    public ResponseEntity<ApiResponse<ProductReviewResponse>> updateReview(
            @PathVariable Long reviewId, @Valid @RequestBody ProductReviewUpdateRequest request) {

        ProductReviewResponse response = productReviewService.updateReview(reviewId, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.<ProductReviewResponse>builder()
                        .message("Sửa đánh giá sản phẩm thành công")
                        .data(response)
                        .build());
    }

    @DeleteMapping("/{reviewId}/delete")
    @Operation(summary = "delete a product review")
    public ResponseEntity<ApiResponse<ProductReviewResponse>> deleteReview(@PathVariable Long reviewId) {

        productReviewService.deleteReview(reviewId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.<ProductReviewResponse>builder()
                        .message("Xóa đánh giá sản phẩm thành công.")
                        .build());
    }
    @GetMapping("/{reviewId}/summary")
    @Operation(summary = "delete a product review")
    public ResponseEntity<ApiResponse<ProductReviewSummaryResponse>> getProductReviewSummary(@PathVariable Long reviewId) {


        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.<ProductReviewSummaryResponse>builder()
                        .message("Lấy thống kê đánh giá sản phẩm thành công.")
                        .data(productReviewService.getProductReviewSummary(reviewId))
                        .build());
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ProductReviewResponse>>> searchReviews(
            ProductReviewSearchRequest searchRequest,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC)
                    Pageable pageable) {
        return ResponseEntity.ok()
                .body(ApiResponse.<Page<ProductReviewResponse>>builder()
                        .message("Lấy danh sách đánh giá sản phẩm thành công.")
                        .data(productReviewService.searchReviews(searchRequest, pageable))
                        .build());
    }
}
