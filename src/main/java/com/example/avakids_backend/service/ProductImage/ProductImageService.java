package com.example.avakids_backend.service.ProductImage;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.avakids_backend.DTO.ProductImage.ProductImageResponse;

public interface ProductImageService {
    ProductImageResponse uploadImage(Long productId, MultipartFile file, Boolean isPrimary);

    List<ProductImageResponse> uploadMultipleImages(Long productId, MultipartFile[] files, Boolean setPrimaryForFirst);

    List<ProductImageResponse> getImagesByProductId(Long productId);

    ProductImageResponse getPrimaryImage(Long productId);

    ProductImageResponse setPrimaryImage(Long imageId);

    ProductImageResponse updateDisplayOrder(Long imageId, Integer displayOrder);

    void deleteImage(Long imageId);

    void deleteAllImagesByProductId(Long productId);
}
