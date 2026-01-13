package com.example.avakids_backend.service.ProductReview;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.avakids_backend.DTO.ProductReview.*;
import com.example.avakids_backend.entity.Order;
import com.example.avakids_backend.entity.Product;
import com.example.avakids_backend.entity.ProductReview;
import com.example.avakids_backend.entity.User;
import com.example.avakids_backend.mapper.ProductReviewMapper;
import com.example.avakids_backend.repository.Product.ProductRepository;
import com.example.avakids_backend.repository.ProductReview.ProductReviewRepository;
import com.example.avakids_backend.service.Authentication.auth.AuthenticationService;
import com.example.avakids_backend.service.Order.OrderValidator;
import com.example.avakids_backend.service.Product.ProductValidator;
import com.example.avakids_backend.util.file.sevrice.FileStorageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductReviewServiceImpl implements ProductReviewService {

    private final ProductReviewRepository productReviewRepository;
    private final ProductReviewMapper productReviewMapper;
    private final ProductReviewValidator productReviewValidator;
    private final AuthenticationService authenticationService;
    private final ProductValidator productValidator;
    private final OrderValidator orderValidator;
    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;
    private static final String REVIEW_IMAGE_FOLDER = "reviews";

    @Override
    @Transactional
    public ProductReviewResponse createReview(ProductReviewCreateRequest request, MultipartFile file) {
        User user = authenticationService.getCurrentUser();
        productReviewValidator.alreadyReviewed(user.getId(), request.getProductId(), request.getOrderId());
        fileStorageService.validateImage(file);

        Product product = productValidator.getProductById(request.getProductId());

        Order order = orderValidator.getOrderById(request.getOrderId());
        productReviewValidator.validateUserPurchase(user, order, product);

        productReviewValidator.validateOrderCompleted(order);
        String imageUrl = fileStorageService.uploadFile(file, REVIEW_IMAGE_FOLDER);
        ProductReview productReview = productReviewMapper.toEntity(request);
        productReview.setImageUrl(imageUrl);
        productReview.setProduct(product);
        productReview.setOrder(order);
        productReview.setUser(user);

        ProductReview savedReview = productReviewRepository.save(productReview);

        updateProductRating(product.getId());

        return productReviewMapper.toResponse(savedReview);
    }

    @Override
    @Transactional
    public ProductReviewResponse updateReview(Long reviewId, ProductReviewUpdateRequest request, MultipartFile file) {
        User user = authenticationService.getCurrentUser();

        ProductReview existingReview = productReviewValidator.getReviewById(reviewId);

        productReviewValidator.existingReviewByProduct(existingReview, user.getId());
        if (request.getContent() != null) {
            existingReview.setContent(request.getContent());
        }

        if (request.getRating() != null) {
            existingReview.setRating(request.getRating());
            updateProductRating(existingReview.getProduct().getId());
        }
        if (file != null && !file.isEmpty()) {
            fileStorageService.validateImage(file);
            fileStorageService.deleteFile(existingReview.getImageUrl());
            String imageUrl = fileStorageService.uploadFile(file, REVIEW_IMAGE_FOLDER);
            existingReview.setImageUrl(imageUrl);
        }

        ProductReview updatedReview = productReviewRepository.save(existingReview);

        return productReviewMapper.toResponse(updatedReview);
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId) {
        User user = authenticationService.getCurrentUser();

        ProductReview review = productReviewValidator.getReviewById(reviewId);

        productReviewValidator.existingReviewByProduct(review, user.getId());

        Long productId = review.getProduct().getId();
        fileStorageService.deleteFile(review.getImageUrl());
        productReviewRepository.delete(review);

        updateProductRating(productId);
    }

    @Override
    public Page<ProductReviewResponse> searchReviews(ProductReviewSearchRequest searchRequest, Pageable pageable) {
        Page<ProductReview> reviews = productReviewRepository.searchReviews(searchRequest, pageable);

        return reviews.map(productReviewMapper::toResponse);
    }

    @Override
    public ProductReviewSummaryResponse getProductReviewSummary(Long productId) {
        productValidator.getProductById(productId);

        ProductReviewStats stats = productReviewRepository.getReviewStats(productId);

        if (stats.getTotalReviews() == 0) {
            List<ProductReviewSummaryResponse.RatingDistribution> ratingDistributions = IntStream.rangeClosed(1, 5)
                    .mapToObj(rating -> ProductReviewSummaryResponse.RatingDistribution.builder()
                            .rating(rating)
                            .count(0)
                            .percentage(0.0)
                            .build())
                    .sorted(Comparator.comparing(ProductReviewSummaryResponse.RatingDistribution::getRating)
                            .reversed())
                    .collect(Collectors.toList());

            return ProductReviewSummaryResponse.builder()
                    .averageRating(BigDecimal.ZERO)
                    .totalReviews(0)
                    .ratingDistribution(ratingDistributions)
                    .verifiedPurchaseCount(0L)
                    .hasImagesCount(0L)
                    .build();
        }

        List<ProductReviewSummaryResponse.RatingDistribution> ratingDistributions =
                stats.getRatingDistribution().entrySet().stream()
                        .map(entry -> ProductReviewSummaryResponse.RatingDistribution.builder()
                                .rating(entry.getKey())
                                .count(entry.getValue().intValue())
                                .percentage(calculatePercentage(entry.getValue(), stats.getTotalReviews()))
                                .build())
                        .sorted(Comparator.comparing(ProductReviewSummaryResponse.RatingDistribution::getRating)
                                .reversed())
                        .collect(Collectors.toList());

        return ProductReviewSummaryResponse.builder()
                .averageRating(stats.getAverageRating())
                .totalReviews(stats.getTotalReviews().intValue())
                .ratingDistribution(ratingDistributions)
                .verifiedPurchaseCount(stats.getVerifiedPurchaseCount())
                .hasImagesCount(stats.getHasImagesCount())
                .build();
    }

    private double calculatePercentage(Long count, Long total) {
        if (total == 0) return 0.0;
        return (count.doubleValue() / total.doubleValue()) * 100.0;
    }

    private void updateProductRating(Long productId) {
        List<ProductReview> reviews = productReviewRepository.findByProductId(productId);

        if (reviews.isEmpty()) {
            productRepository.updateProductRating(productId, BigDecimal.ZERO, 0);
            return;
        }

        double averageRating =
                reviews.stream().mapToInt(ProductReview::getRating).average().orElse(0.0);

        BigDecimal roundedRating = BigDecimal.valueOf(averageRating).setScale(2, RoundingMode.HALF_UP);

        productRepository.updateProductRating(productId, roundedRating, reviews.size());
    }
}
