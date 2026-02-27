package com.example.avakids_backend.service.Wishlist;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.avakids_backend.DTO.Product.ProductResponse;
import com.example.avakids_backend.DTO.Wishlist.WishlistCreateRequest;
import com.example.avakids_backend.DTO.Wishlist.WishlistResponse;
import com.example.avakids_backend.DTO.Wishlist.WishlistSearchRequest;
import com.example.avakids_backend.entity.Product;
import com.example.avakids_backend.entity.User;
import com.example.avakids_backend.entity.Wishlist;
import com.example.avakids_backend.enums.FollowTargetType;
import com.example.avakids_backend.mapper.WishlistMapper;
import com.example.avakids_backend.repository.ProductImage.ProductImageRepository;
import com.example.avakids_backend.repository.Wishlist.WishlistRepository;
import com.example.avakids_backend.service.Authentication.auth.AuthenticationService;
import com.example.avakids_backend.service.Notification.NotificationServiceImpl;
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
    private final ProductImageRepository productImageRepository;
    private final NotificationServiceImpl notificationServiceImpl;

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
            notificationServiceImpl.updateCarFollowNotify(userId, FollowTargetType.PRODUCT, productId, false);
            return null;
        } else {
            Wishlist wishlist = Wishlist.builder()
                    .user(user)
                    .product(product)
                    .createdAt(LocalDateTime.now())
                    .build();

            Wishlist savedWishlist = wishlistRepository.save(wishlist);
            notificationServiceImpl.updateCarFollowNotify(userId, FollowTargetType.PRODUCT, productId, true);
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

    @Override
    public List<ProductResponse> getMyWishlist(int limit) {

        User user = authenticationService.getCurrentUser();

        List<Product> products = wishlistRepository.findFavoriteProducts(user.getId(), limit);

        if (products.isEmpty()) {
            return List.of();
        }

        List<Long> productIds = products.stream().map(Product::getId).toList();

        Map<Long, String> imageMap = productImageRepository.loadPrimaryImages(productIds);

        return products.stream()
                .map(product -> ProductResponse.builder()
                        .id(product.getId())
                        .sku(product.getSku())
                        .name(product.getName())
                        .imageUlr(imageMap != null ? imageMap.get(product.getId()) : null)
                        .description(product.getDescription())
                        .hasVariants(product.getHasVariants())
                        .price(product.getPrice())
                        .salePrice(product.getSalePrice())
                        .minPrice(product.getMinPrice())
                        .maxPrice(product.getMaxPrice())
                        .totalStock(product.getTotalStock())
                        .isActive(product.getIsActive())
                        .isFeatured(product.getIsFeatured())
                        .avgRating(product.getAvgRating())
                        .reviewCount(product.getReviewCount())
                        .soldCount(product.getSoldCount())
                        .isFavorite(true) // Vì đang ở wishlist
                        .createdAt(product.getCreatedAt())
                        .updatedAt(product.getUpdatedAt())
                        .build())
                .toList();
    }
}
