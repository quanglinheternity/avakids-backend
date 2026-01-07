package com.example.avakids_backend.service.CartItem;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.avakids_backend.DTO.CartItem.CartItemResponse;
import com.example.avakids_backend.DTO.CartItem.CartSummaryResponse;

public interface CartItemService {

    CartItemResponse addToCart(Long productId, Integer quantity);

    CartItemResponse updateCartItemQuantity(Long cartItemId, Integer quantity);

    void removeFromCart(Long cartItemId);
    //
    //    // Get all cart items for a user
    List<CartItemResponse> getCartItems();
    //
    //    // Get cart summary
    CartSummaryResponse getCartSummary();
    //
    void removeOutOfStockItems();

    Page<CartItemResponse> searchCartItems(
            Long productId, String keyWord, Integer minQuantity, Integer maxQuantity, Pageable pageable);

    void clearCart();
    //    // Clear entire cart
    //    void clearCart(Long userId);
    //
    //    // Get cart item by ID
    //    CartItemResponse getCartItemById(Long userId, Long cartItemId);
    //
    //    // Check if product is in cart
    //    boolean isProductInCart(Long userId, Long productId);
    //
    //    // Search cart items with filters
    //    Page<CartItemResponse> searchCartItems(Long userId, Long productId,
    //                                      Integer minQuantity, Integer maxQuantity,
    //                                      Pageable pageable);
    //
    //    // Get cart items with low stock warning
    //    List<CartItemResponse> getCartItemsWithLowStock(Long userId, Integer stockThreshold);
    //
    //    // Sync cart (validate stock availability)
    //    List<CartItemResponse> syncCart(Long userId);
    //
    //    // Remove out of stock items
    //    void removeOutOfStockItems(Long userId);
}
