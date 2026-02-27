package com.example.avakids_backend.repository.Wishlist;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.avakids_backend.DTO.Wishlist.WishlistSearchRequest;
import com.example.avakids_backend.entity.Product;
import com.example.avakids_backend.entity.Wishlist;

public interface WishlistRepositoryCustom {
    Page<Wishlist> searchWishlists(WishlistSearchRequest request, Long userId, Pageable pageable);

    List<Long> findProductIdsByUserId(Long userId);

    List<Product> findFavoriteProducts(Long userId, int limit);
}
