package com.example.avakids_backend.service.CartItem;

import org.springframework.stereotype.Component;

import com.example.avakids_backend.entity.CartItem;
import com.example.avakids_backend.entity.Product;
import com.example.avakids_backend.exception.AppException;
import com.example.avakids_backend.exception.ErrorCode;
import com.example.avakids_backend.repository.CartItem.CartItemRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CartItemValidator {
    private final CartItemRepository cartItemRepository;

    public CartItem getCartItemById(Long cartItemId, Long userId) {
        return cartItemRepository
                .findByIdAndUserId(cartItemId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_ALREADY_EXISTS));
    }

    public void validateUpdateQuantity(CartItem cartItem, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new AppException(ErrorCode.PRODUCT_QUANTITY_MIN);
        }

        validateStockQuantity(cartItem.getProduct().getStockQuantity(), quantity);
    }

    public void validateAddQuantity(Product product, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new AppException(ErrorCode.PRODUCT_QUANTITY_MIN);
        }

        validateStockQuantity(product.getStockQuantity(), quantity);
    }

    public void validateUpdateQuantity(Product product, CartItem cartItem, Integer addQuantity) {
        int newQuantity = cartItem.getQuantity() + addQuantity;
        validateStockQuantity(product.getStockQuantity(), newQuantity);
    }

    private void validateStockQuantity(Integer stockQuantity, Integer addQuantity) {
        if (stockQuantity < addQuantity) {
            throw new AppException(ErrorCode.PRODUCT_OUT_OF_STOCK);
        }
    }
}
