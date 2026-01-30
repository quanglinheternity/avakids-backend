package com.example.avakids_backend.service.ProductImage;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.avakids_backend.DTO.ProductImage.ProductImageResponse;
import com.example.avakids_backend.entity.Product;
import com.example.avakids_backend.entity.ProductImage;
import com.example.avakids_backend.exception.AppException;
import com.example.avakids_backend.exception.ErrorCode;
import com.example.avakids_backend.mapper.ProductImageMapper;
import com.example.avakids_backend.repository.ProductImage.ProductImageRepository;
import com.example.avakids_backend.service.Product.ProductValidator;
import com.example.avakids_backend.util.file.sevrice.FileStorageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductImageValidator productImageValidator;
    private final ProductValidator productValidator;
    private final FileStorageService fileStorageService;
    private final ProductImageMapper productImageMapper;

    private static final String PRODUCT_IMAGE_FOLDER = "products";

    @Transactional
    @Override
    public ProductImageResponse uploadImage(Long productId, MultipartFile file, Boolean isPrimary) {
        Product product = productValidator.getProductById(productId);

        fileStorageService.validateImage(file);
        productImageValidator.validateMaxImageById(productId);

        String imageUrl = fileStorageService.uploadFile(file, PRODUCT_IMAGE_FOLDER);

        if (Boolean.TRUE.equals(isPrimary)) {
            productImageRepository.resetPrimaryImagesByProductId(productId);
        }

        Integer displayOrder = productImageValidator.getNextDisplayOrder(productId);

        ProductImage productImage = ProductImage.builder()
                .product(product)
                .imageUrl(imageUrl)
                .displayOrder(displayOrder)
                .isPrimary(isPrimary != null ? isPrimary : false)
                .build();

        ProductImage saved = productImageRepository.save(productImage);
        log.info("Uploaded image for product {}: {}", productId, imageUrl);

        return productImageMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public List<ProductImageResponse> uploadMultipleImages(
            Long productId, MultipartFile[] files, Boolean setPrimaryForFirst) {
        Product product = productValidator.getProductById(productId);

        productImageValidator.validateFiles(files);

        productImageValidator.validateMaxImages(productId, files.length);

        List<String> imageUrls = fileStorageService.uploadMultipleImages(files, PRODUCT_IMAGE_FOLDER);

        boolean shouldSetPrimary = Boolean.TRUE.equals(setPrimaryForFirst);
        if (shouldSetPrimary) {
            productImageRepository.resetPrimaryImagesByProductId(productId);
        }

        Integer startDisplayOrder = productImageValidator.getNextDisplayOrder(productId);
        List<ProductImage> productImages = new java.util.ArrayList<>();

        for (int i = 0; i < imageUrls.size(); i++) {
            ProductImage productImage = ProductImage.builder()
                    .product(product)
                    .imageUrl(imageUrls.get(i))
                    .displayOrder(startDisplayOrder + i)
                    .isPrimary(shouldSetPrimary && i == 0)
                    .build();
            productImages.add(productImage);
        }

        List<ProductImage> saved = productImageRepository.saveAll(productImages);
        log.info("Uploaded {} images for product {}", saved.size(), productId);

        return saved.stream().map(productImageMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<ProductImageResponse> getImagesByProductId(Long productId) {
        List<ProductImage> images = productImageRepository.findByProductIdOrderByDisplayOrderAsc(productId);
        return images.stream().map(productImageMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public ProductImageResponse getPrimaryImage(Long productId) {
        ProductImage image = productImageRepository
                .findPrimaryImageByProductId(productId)
                .orElseThrow(() -> new AppException(ErrorCode.FILE_NOT_IMAGE));
        return productImageMapper.toResponse(image);
    }

    @Override
    @Transactional
    public ProductImageResponse setPrimaryImage(Long imageId) {
        ProductImage image =
                productImageRepository.findById(imageId).orElseThrow(() -> new AppException(ErrorCode.FILE_NOT_IMAGE));

        productImageRepository.resetPrimaryImagesByProductId(image.getProduct().getId());

        image.setIsPrimary(true);
        ProductImage updated = productImageRepository.save(image);

        log.info(
                "Set image {} as primary for product {}",
                imageId,
                image.getProduct().getId());
        return productImageMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public ProductImageResponse updateDisplayOrder(Long imageId, Integer displayOrder) {
        ProductImage image =
                productImageRepository.findById(imageId).orElseThrow(() -> new AppException(ErrorCode.FILE_NOT_IMAGE));

        image.setDisplayOrder(displayOrder);
        ProductImage updated = productImageRepository.save(image);

        return productImageMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteImage(Long imageId) {
        ProductImage image =
                productImageRepository.findById(imageId).orElseThrow(() -> new AppException(ErrorCode.FILE_NOT_IMAGE));

        fileStorageService.deleteFile(image.getImageUrl());

        productImageRepository.delete(image);

        log.info("Deleted image {} for product {}", imageId, image.getProduct().getId());
    }

    @Override
    @Transactional
    public void deleteAllImagesByProductId(Long productId) {
        List<ProductImage> images = productImageRepository.findByProductIdOrderByDisplayOrderAsc(productId);

        List<String> imageUrls = images.stream().map(ProductImage::getImageUrl).collect(Collectors.toList());
        fileStorageService.deleteMultipleFiles(imageUrls);

        productImageRepository.deleteByProductId(productId);

        log.info("Deleted all images for product {}", productId);
    }
}
