package com.example.avakids_backend.service.ProductVariantImage;

import java.util.List;
import java.util.stream.Collectors;

import com.example.avakids_backend.util.file.sevrice.CloudService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.avakids_backend.DTO.ProductImage.ProductImageResponse;
import com.example.avakids_backend.entity.ProductVariant;
import com.example.avakids_backend.entity.ProductVariantImage;
import com.example.avakids_backend.exception.AppException;
import com.example.avakids_backend.exception.ErrorCode;
import com.example.avakids_backend.mapper.ProductVariantImageMapper;
import com.example.avakids_backend.repository.ProductVariantImage.ProductVariantImageRepository;
import com.example.avakids_backend.service.ProductVariant.ProductVariantValidator;
import com.example.avakids_backend.util.file.sevrice.FileStorageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductVariantImageServiceImpl implements ProductVariantImageService {

    private final ProductVariantImageRepository variantImageRepository;
    private final ProductVariantImageValidator productImageValidator;
    private final ProductVariantValidator productValidator;
    private final CloudService fileStorageService;
    private final ProductVariantImageMapper variantImageMapper;

    private static final String PRODUCT_IMAGE_FOLDER = "productVariants";

    @Transactional
    @Override
    public ProductImageResponse uploadImage(Long variantId, MultipartFile file, Boolean isPrimary) {
        ProductVariant validator = productValidator.getVariantById(variantId);

        fileStorageService.validateImage(file);
        productImageValidator.validateMaxImageById(variantId);

        String imageUrl = fileStorageService.uploadFile(file, PRODUCT_IMAGE_FOLDER);

        if (Boolean.TRUE.equals(isPrimary)) {
            variantImageRepository.resetPrimaryImagesByVariantId(variantId);
        }

        Integer displayOrder = productImageValidator.getNextDisplayOrder(variantId);

        ProductVariantImage productImage = ProductVariantImage.builder()
                .variant(validator)
                .imageUrl(imageUrl)
                .displayOrder(displayOrder)
                .isPrimary(isPrimary != null ? isPrimary : false)
                .build();

        ProductVariantImage saved = variantImageRepository.save(productImage);
        log.info("Uploaded image for product {}: {}", variantId, imageUrl);

        return variantImageMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public List<ProductImageResponse> uploadMultipleImages(
            Long variantId, MultipartFile[] files, Boolean setPrimaryForFirst) {
        ProductVariant validator = productValidator.getVariantById(variantId);

        productImageValidator.validateFiles(files);

        productImageValidator.validateMaxImages(variantId, files.length);

        List<String> imageUrls = fileStorageService.uploadMultipleImages(files, PRODUCT_IMAGE_FOLDER);

        boolean shouldSetPrimary = Boolean.TRUE.equals(setPrimaryForFirst);
        if (shouldSetPrimary) {
            variantImageRepository.resetPrimaryImagesByVariantId(variantId);
        }

        Integer startDisplayOrder = productImageValidator.getNextDisplayOrder(variantId);
        List<ProductVariantImage> productImages = new java.util.ArrayList<>();

        for (int i = 0; i < imageUrls.size(); i++) {
            ProductVariantImage variantImage = ProductVariantImage.builder()
                    .variant(validator)
                    .imageUrl(imageUrls.get(i))
                    .displayOrder(startDisplayOrder + i)
                    .isPrimary(shouldSetPrimary && i == 0)
                    .build();
            productImages.add(variantImage);
        }

        List<ProductVariantImage> saved = variantImageRepository.saveAll(productImages);
        log.info("Uploaded {} images for product {}", saved.size(), variantId);

        return saved.stream().map(variantImageMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<ProductImageResponse> getImagesByProductId(Long variantId) {
        List<ProductVariantImage> images = variantImageRepository.findByVariantIdOrderByDisplayOrderAsc(variantId);
        return images.stream().map(variantImageMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public ProductImageResponse getPrimaryImage(Long productId) {
        ProductVariantImage image = variantImageRepository
                .findPrimaryImageByVariantId(productId)
                .orElseThrow(() -> new AppException(ErrorCode.FILE_NOT_IMAGE));
        return variantImageMapper.toResponse(image);
    }

    @Override
    @Transactional
    public ProductImageResponse setPrimaryImage(Long imageId) {
        ProductVariantImage image =
                variantImageRepository.findById(imageId).orElseThrow(() -> new AppException(ErrorCode.FILE_NOT_IMAGE));

        variantImageRepository.resetPrimaryImagesByVariantId(image.getVariant().getId());

        image.setIsPrimary(true);
        ProductVariantImage updated = variantImageRepository.save(image);

        log.info(
                "Set image {} as primary for product {}",
                imageId,
                image.getVariant().getId());
        return variantImageMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public ProductImageResponse updateDisplayOrder(Long imageId, Integer displayOrder) {
        ProductVariantImage image =
                variantImageRepository.findById(imageId).orElseThrow(() -> new AppException(ErrorCode.FILE_NOT_IMAGE));

        image.setDisplayOrder(displayOrder);
        ProductVariantImage updated = variantImageRepository.save(image);

        return variantImageMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteImage(Long imageId) {
        ProductVariantImage image =
                variantImageRepository.findById(imageId).orElseThrow(() -> new AppException(ErrorCode.FILE_NOT_IMAGE));

        fileStorageService.deleteFile(image.getImageUrl());

        variantImageRepository.delete(image);

        log.info("Deleted image {} for product {}", imageId, image.getVariant().getId());
    }

    @Override
    @Transactional
    public void deleteAllImagesByProductId(Long productId) {
        List<ProductVariantImage> images = variantImageRepository.findByVariantIdOrderByDisplayOrderAsc(productId);

        List<String> imageUrls =
                images.stream().map(ProductVariantImage::getImageUrl).collect(Collectors.toList());
        fileStorageService.deleteMultipleFiles(imageUrls);

        variantImageRepository.deleteByVariantId(productId);

        log.info("Deleted all images for product {}", productId);
    }
}
