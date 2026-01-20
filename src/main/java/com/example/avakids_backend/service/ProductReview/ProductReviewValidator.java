package com.example.avakids_backend.service.ProductReview;

import java.util.EnumSet;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.example.avakids_backend.entity.Order;
import com.example.avakids_backend.entity.Product;
import com.example.avakids_backend.entity.ProductReview;
import com.example.avakids_backend.entity.User;
import com.example.avakids_backend.enums.OrderStatus;
import com.example.avakids_backend.exception.AppException;
import com.example.avakids_backend.exception.ErrorCode;
import com.example.avakids_backend.repository.ProductReview.ProductReviewRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductReviewValidator {
    private final ProductReviewRepository productReviewRepository;

    public void alreadyReviewed(Long userId, Long productId, Long orderId) {
        boolean alreadyReviewed =
                productReviewRepository.existsByUserIdAndProductIdAndOrderId(userId, productId, orderId);

        if (alreadyReviewed) {
            throw new AppException(ErrorCode.PRODUCT_REVIEW_ALREADY_EXISTS);
        }
    }

    public void validateUserPurchase(User user, Order order, Product product) {
        if (!order.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.ORDER_USER_NOT_NULL);
        }

        boolean productInOrder = order.getOrderItems().stream()
                .anyMatch(item -> item.getVariant().getProduct().getId().equals(product.getId()));

        if (!productInOrder) {
            throw new AppException(ErrorCode.PRODUCT_CATEGORY_NOT_NULL);
        }
    }

    public void validateOrderCompleted(Order order) {
        Set<OrderStatus> allowedStatuses = EnumSet.of(
                OrderStatus.DELIVERED
                // OrderStatus.COMPLETED
                );

        if (!allowedStatuses.contains(order.getStatus())) {
            throw new AppException(ErrorCode.PRODUCT_DELIVERED_NOT_NULL);
        }
    }

    public ProductReview getReviewById(Long reviewId) {
        return productReviewRepository
                .findById(reviewId)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_NULL));
    }

    public void existingReviewByProduct(ProductReview existingReview, Long userId) {
        if (!existingReview.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.USER_NOT_ACCESS);
        }
    }
}
