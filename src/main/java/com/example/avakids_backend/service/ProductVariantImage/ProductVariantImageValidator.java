package com.example.avakids_backend.service.ProductVariantImage;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.example.avakids_backend.exception.AppException;
import com.example.avakids_backend.exception.ErrorCode;
import com.example.avakids_backend.repository.ProductVariantImage.ProductVariantImageRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductVariantImageValidator {
    private final ProductVariantImageRepository variantImageRepository;
    protected static final int MAX_IMAGES_PER_PRODUCT = 10;

    public void validateMaxImageById(Long productId) {
        long count = variantImageRepository.countByVariantId(productId);
        if (count >= MAX_IMAGES_PER_PRODUCT) {
            throw new AppException(ErrorCode.TOO_MANY_IMAGES);
        }
    }

    public Integer getNextDisplayOrder(Long productId) {
        Integer maxOrder = variantImageRepository.findMaxDisplayOrderByVariantId(productId);
        return (maxOrder != null ? maxOrder : -1) + 1;
    }

    public void validateFiles(MultipartFile[] files) {
        if (files == null || files.length == 0) {
            throw new AppException(ErrorCode.FILE_EMPTY);
        }
    }

    public void validateMaxImages(Long productId, int uploadCount) {
        long currentCount = variantImageRepository.countByVariantId(productId);
        if (currentCount + uploadCount > MAX_IMAGES_PER_PRODUCT) {
            throw new AppException(ErrorCode.TOO_MANY_IMAGES);
        }
    }
}
