package com.example.avakids_backend.controller.ProductImage;

import java.util.List;

import jakarta.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.avakids_backend.DTO.ApiResponse;
import com.example.avakids_backend.DTO.ProductImage.ProductImageResponse;
import com.example.avakids_backend.service.ProductImage.ProductImageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/product-images")
@RequiredArgsConstructor
@Tag(name = "Product Image Management", description = "APIs for managing product images and galleries")
public class ProductImageController {

    private final ProductImageService productImageService;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            summary = "Upload single product image",
            description = "Upload a single image for a product with optional primary image flag")
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<ProductImageResponse>> uploadImage(
            @RequestParam("productId") @NotNull Long productId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "isPrimary", required = false) Boolean isPrimary) {

        ProductImageResponse response = productImageService.uploadImage(productId, file, isPrimary);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<ProductImageResponse>builder()
                        .code(HttpStatus.CREATED.value())
                        .message("Tạo ảnh thành công.")
                        .data(response)
                        .build());
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            summary = "Upload multiple product images",
            description = "Upload multiple images for a product in one request")
    @PostMapping("/upload-multiple")
    public ResponseEntity<ApiResponse<List<ProductImageResponse>>> uploadMultipleImages(
            @RequestParam("productId") @NotNull Long productId,
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "setPrimaryForFirst", required = false) Boolean setPrimaryForFirst) {

        List<ProductImageResponse> responses =
                productImageService.uploadMultipleImages(productId, files, setPrimaryForFirst);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<List<ProductImageResponse>>builder()
                        .code(HttpStatus.CREATED.value())
                        .message("Tạo nhiều ảnh thành công.")
                        .data(responses)
                        .build());
    }

    @Operation(
            summary = "Get all images for a product",
            description = "Retrieve all images associated with a specific product")
    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<List<ProductImageResponse>>> getImagesByProductId(@PathVariable Long productId) {

        List<ProductImageResponse> responses = productImageService.getImagesByProductId(productId);

        return ResponseEntity.ok(ApiResponse.<List<ProductImageResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Lấy tất cả ảnh thành công.")
                .data(responses)
                .build());
    }

    @Operation(
            summary = "Get primary image for a product",
            description = "Retrieve the primary/main image of a product")
    @GetMapping("/product/{productId}/primary")
    public ResponseEntity<ApiResponse<ProductImageResponse>> getPrimaryImage(@PathVariable Long productId) {

        ProductImageResponse response = productImageService.getPrimaryImage(productId);

        return ResponseEntity.ok(ApiResponse.<ProductImageResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Lấy ảnh mặc định thành công.")
                .data(response)
                .build());
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            summary = "Set image as primary",
            description = "Set a specific image as the primary image for its product")
    @PutMapping("/{imageId}/set-primary")
    public ResponseEntity<ApiResponse<ProductImageResponse>> setPrimaryImage(@PathVariable Long imageId) {

        ProductImageResponse response = productImageService.setPrimaryImage(imageId);

        return ResponseEntity.ok(ApiResponse.<ProductImageResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Cập nhật ảnh làm ảnh mặc định thành ")
                .data(response)
                .build());
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            summary = "Update image display order",
            description = "Update the display order/sorting position of an image in the product gallery")
    @PutMapping("/{imageId}/display-order")
    public ResponseEntity<ApiResponse<ProductImageResponse>> updateDisplayOrder(
            @PathVariable Long imageId, @RequestParam Integer displayOrder) {

        ProductImageResponse response = productImageService.updateDisplayOrder(imageId, displayOrder);

        return ResponseEntity.ok(ApiResponse.<ProductImageResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Cập nhật thứ tự hiển thị thành công.")
                .data(response)
                .build());
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "Delete a single image", description = "Delete a specific product image by ID")
    @DeleteMapping("/{imageId}")
    public ResponseEntity<ApiResponse<Void>> deleteImage(@PathVariable Long imageId) {

        productImageService.deleteImage(imageId);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Xóa ảnh thành công.")
                .build());
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            summary = "Delete all images for a product",
            description = "Delete all images associated with a specific product")
    @DeleteMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteAllImagesByProductId(@PathVariable Long productId) {

        productImageService.deleteAllImagesByProductId(productId);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Xóa tất ả ảnh thành công.")
                .build());
    }
}
