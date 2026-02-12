package com.example.avakids_backend.repository.CartItem;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.avakids_backend.DTO.CartItem.CartItemResponse;
import com.example.avakids_backend.entity.CartItem;

public interface CartItemRepositoryCustom {

    List<CartItemResponse> findCartItemResponses(Long userId);

    Page<CartItem> searchCartItems(
            Long userId, String keyWord, Long productId, Integer minQuantity, Integer maxQuantity, Pageable pageable);

    void deleteOutOfStockItems(Long userId);
}
