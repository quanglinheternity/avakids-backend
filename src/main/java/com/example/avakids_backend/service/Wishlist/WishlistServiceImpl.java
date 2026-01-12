package com.example.avakids_backend.service.Wishlist;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.avakids_backend.DTO.Wishlist.WishlistCreateRequest;
import com.example.avakids_backend.DTO.Wishlist.WishlistResponse;
import com.example.avakids_backend.DTO.Wishlist.WishlistSearchRequest;
import com.example.avakids_backend.entity.Product;
import com.example.avakids_backend.entity.User;
import com.example.avakids_backend.entity.Wishlist;
import com.example.avakids_backend.mapper.WishlistMapper;
import com.example.avakids_backend.repository.Wishlist.WishlistRepository;
import com.example.avakids_backend.service.Authentication.auth.AuthenticationService;
import com.example.avakids_backend.service.Product.ProductValidator;
import com.example.avakids_backend.service.User.UserValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class WishlistServiceImpl implements WishlistService {
    private final WishlistRepository wishlistRepository;
    private final WishValidator wishValidator;
    private final WishlistMapper wishlistMapper;
    private final AuthenticationService authenticationService;
    private final UserValidator userValidator;
    private final ProductValidator productValidator;

    @Override
    public WishlistResponse addToWishlist(WishlistCreateRequest request) {
        Long userId = authenticationService.getCurrentUser().getId();

        wishValidator.existsByUserIdAndProductId(userId, request.getProductId());

        User user = userValidator.validateUserExists(userId);

        Product product = productValidator.getProductByIdAndIsActive(request.getProductId());

        Wishlist wishlist = Wishlist.builder()
                .user(user)
                .product(product)
                .createdAt(LocalDateTime.now())
                .build();

        Wishlist savedWishlist = wishlistRepository.save(wishlist);
        return wishlistMapper.toResponse(savedWishlist);
    }

    @Override
    public void removeFromWishlist(WishlistCreateRequest request) {
        Long userId = authenticationService.getCurrentUser().getId();

        wishValidator.validateExists(userId, request.getProductId());

        wishlistRepository.deleteByUserIdAndProductId(userId, request.getProductId());
    }

    @Override
    @Transactional
    public WishlistResponse toggleWishlist(WishlistCreateRequest request) {

        Long userId = authenticationService.getCurrentUser().getId();
        Long productId = request.getProductId();
        User user = userValidator.validateUserExists(userId);
        Product product = productValidator.getProductByIdAndIsActive(productId);
        boolean exists = wishlistRepository.existsByUserIdAndProductId(userId, productId);
        if (exists) {
            wishlistRepository.deleteByUserIdAndProductId(userId, productId);
            return null;
        } else {
            Wishlist wishlist = Wishlist.builder()
                    .user(user)
                    .product(product)
                    .createdAt(LocalDateTime.now())
                    .build();

            Wishlist savedWishlist = wishlistRepository.save(wishlist);

            return wishlistMapper.toResponse(savedWishlist);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WishlistResponse> searchWishlists(WishlistSearchRequest request, Pageable pageable) {
        Long userId = authenticationService.getCurrentUser().getId();

        Page<Wishlist> wishlistPage = wishlistRepository.searchWishlists(request, userId, pageable);
        return wishlistPage.map(wishlistMapper::toResponse);
    }
}
