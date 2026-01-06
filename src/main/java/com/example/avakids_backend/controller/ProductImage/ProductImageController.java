package com.example.avakids_backend.controller.ProductImage;

import java.util.List;

import jakarta.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@Tag(name = "product images", description = "APIs for managing product images")
public class ProductImageController {

    private final ProductImageService productImageService;

    @Operation(summary = "Upload một ảnh cho product")
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

    @Operation(summary = "Upload nhiều ảnh cho product")
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

    @Operation(summary = "Lấy tất cả ảnh của product")
    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<List<ProductImageResponse>>> getImagesByProductId(@PathVariable Long productId) {

        List<ProductImageResponse> responses = productImageService.getImagesByProductId(productId);

        return ResponseEntity.ok(ApiResponse.<List<ProductImageResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Lấy tất cả ảnh thành công.")
                .data(responses)
                .build());
    }

    @Operation(summary = "Lấy ảnh primary của product")
    @GetMapping("/product/{productId}/primary")
    public ResponseEntity<ApiResponse<ProductImageResponse>> getPrimaryImage(@PathVariable Long productId) {

        ProductImageResponse response = productImageService.getPrimaryImage(productId);

        return ResponseEntity.ok(ApiResponse.<ProductImageResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Lấy ảnh mặc định thành công.")
                .data(response)
                .build());
    }

    @Operation(summary = "Set ảnh làm primary")
    @PutMapping("/{imageId}/set-primary")
    public ResponseEntity<ApiResponse<ProductImageResponse>> setPrimaryImage(@PathVariable Long imageId) {

        ProductImageResponse response = productImageService.setPrimaryImage(imageId);

        return ResponseEntity.ok(ApiResponse.<ProductImageResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Cập nhật ảnh làm ảnh mặc định thành ")
                .data(response)
                .build());
    }

    @Operation(summary = "Cập nhật thứ tự hiển thị")
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

    @Operation(summary = "Xóa một ảnh")
    @DeleteMapping("/{imageId}")
    public ResponseEntity<ApiResponse<Void>> deleteImage(@PathVariable Long imageId) {

        productImageService.deleteImage(imageId);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Xóa ảnh thành công.")
                .build());
    }

    @Operation(summary = "Xóa tất cả ảnh của product")
    @DeleteMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteAllImagesByProductId(@PathVariable Long productId) {

        productImageService.deleteAllImagesByProductId(productId);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Xóa tất ả ảnh thành công.")
                .build());
    }
}
