package com.example.avakids_backend.service.Wishlist;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.avakids_backend.DTO.Wishlist.WishlistCreateRequest;
import com.example.avakids_backend.DTO.Wishlist.WishlistResponse;
import com.example.avakids_backend.DTO.Wishlist.WishlistSearchRequest;

public interface WishlistService {
    WishlistResponse addToWishlist(WishlistCreateRequest request);

    void removeFromWishlist(WishlistCreateRequest request);

    Page<WishlistResponse> searchWishlists(WishlistSearchRequest request, Pageable pageable);

    WishlistResponse toggleWishlist(WishlistCreateRequest request);
    //    void removeWishlistItem(Long id);
    //    WishlistResponse getWishlistItem(Long id);
    //    List<WishlistDTO> getUserWishlist(Long userId);
    //    Page<WishlistDTO> getUserWishlist(Long userId, Pageable pageable);
    //    List<WishlistResponse> getUserWishlistWithDetails(Long userId);
    //    boolean isProductInWishlist(Long userId, Long productId);
    //    int countUserWishlistItems(Long userId);
    //    void clearUserWishlist(Long userId);
    //    List<WishlistDTO> getActiveProductsFromWishlist(Long userId);
}
