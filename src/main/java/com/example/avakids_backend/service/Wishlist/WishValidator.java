package com.example.avakids_backend.service.Wishlist;

import org.springframework.stereotype.Component;

import com.example.avakids_backend.exception.AppException;
import com.example.avakids_backend.exception.ErrorCode;
import com.example.avakids_backend.repository.Wishlist.WishlistRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WishValidator {
    private final WishlistRepository wishlistRepository;

    public void existsByUserIdAndProductId(Long userId, Long productId) {
        if (wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new AppException(ErrorCode.WISH_ALREADY_EXISTS);
        }
    }

    public void validateExists(Long userId, Long productId) {
        if (!wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new AppException(ErrorCode.WISH_NOT_FOUND);
        }
    }
}
