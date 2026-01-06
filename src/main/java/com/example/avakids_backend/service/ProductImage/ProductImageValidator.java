package com.example.avakids_backend.service.ProductImage;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.example.avakids_backend.exception.AppException;
import com.example.avakids_backend.exception.ErrorCode;
import com.example.avakids_backend.repository.ProductImage.ProductImageRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductImageValidator {
    private final ProductImageRepository productImageRepository;
    protected static final int MAX_IMAGES_PER_PRODUCT = 10;

    public void validateMaxImageById(Long productId) {
        long count = productImageRepository.countByProductId(productId);
        if (count >= MAX_IMAGES_PER_PRODUCT) {
            throw new AppException(ErrorCode.TOO_MANY_IMAGES);
        }
    }

    public Integer getNextDisplayOrder(Long productId) {
        Integer maxOrder = productImageRepository.findMaxDisplayOrderByProductId(productId);
        return (maxOrder != null ? maxOrder : -1) + 1;
    }

    public void validateFiles(MultipartFile[] files) {
        if (files == null || files.length == 0) {
            throw new AppException(ErrorCode.FILE_EMPTY);
        }
    }

    public void validateMaxImages(Long productId, int uploadCount) {
        long currentCount = productImageRepository.countByProductId(productId);
        if (currentCount + uploadCount > MAX_IMAGES_PER_PRODUCT) {
            throw new AppException(ErrorCode.TOO_MANY_IMAGES);
        }
    }
}
