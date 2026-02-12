package com.example.avakids_backend.service.CartItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.avakids_backend.DTO.CartItem.CartItemResponse;
import com.example.avakids_backend.DTO.CartItem.CartSummaryResponse;
import com.example.avakids_backend.entity.CartItem;
import com.example.avakids_backend.entity.ProductVariant;
import com.example.avakids_backend.entity.User;
import com.example.avakids_backend.mapper.CartItemMapper;
import com.example.avakids_backend.repository.CartItem.CartItemRepository;
import com.example.avakids_backend.service.Authentication.auth.AuthenticationService;
import com.example.avakids_backend.service.ProductVariant.ProductVariantValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;
    private final CartItemValidator cartItemValidator;
    private final AuthenticationService authenticationService;
    private final ProductVariantValidator productVariantValidator;
    private final CartItemMapper cartItemMapper;

    @Override
    public CartItemResponse addToCart(Long variantId, Integer quantity) {

        User user = authenticationService.getCurrentUser();

        ProductVariant validator = productVariantValidator.getVariantById(variantId);
        cartItemValidator.validateAddQuantity(validator, quantity);

        CartItem cartItem = cartItemRepository
                .findByUserIdAndVariantId(user.getId(), variantId)
                .map(existing -> {
                    cartItemValidator.validateUpdateQuantity(validator, existing, quantity);
                    existing.setQuantity(existing.getQuantity() + quantity);
                    return existing;
                })
                .orElseGet(() -> CartItem.builder()
                        .user(user)
                        .variant(validator)
                        .quantity(quantity)
                        .build());

        cartItem = cartItemRepository.save(cartItem);

        return cartItemMapper.toDTO(cartItem);
    }

    @Transactional
    @Override
    public CartItemResponse updateCartItemQuantity(Long cartItemId, Integer quantity) {
        log.info("cartItemId = {}, quantity = {}", cartItemId, quantity);
        User user = authenticationService.getCurrentUser();

        CartItem cartItem = cartItemValidator.getCartItemById(cartItemId, user.getId());
        cartItemValidator.validateUpdateQuantity(cartItem, quantity);

        cartItem.setQuantity(quantity);

        return cartItemMapper.toDTO(cartItem);
    }

    @Transactional
    @Override
    public void removeFromCart(Long cartItemId) {
        User user = authenticationService.getCurrentUser();

        CartItem cartItem = cartItemValidator.getCartItemById(cartItemId, user.getId());

        cartItemRepository.delete(cartItem);
        log.info("Successfully removed cart item");
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartItemResponse> getCartItems() {
        Long userId = authenticationService.getCurrentUser().getId();

        return cartItemRepository.findCartItemResponses(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public CartSummaryResponse getCartSummary() {

        List<CartItemResponse> items = getCartItems();

        Integer totalItems =
                items.stream().mapToInt(CartItemResponse::getQuantity).sum();

        BigDecimal totalAmount =
                items.stream().map(CartItemResponse::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);

        LocalDateTime lastUpdated = items.stream()
                .map(CartItemResponse::getUpdatedAt)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        return CartSummaryResponse.builder()
                .items(items)
                .totalItems(totalItems)
                .totalAmount(totalAmount)
                .lastUpdated(lastUpdated)
                .build();
    }

    @Transactional
    @Override
    public void removeOutOfStockItems() {
        Long userId = authenticationService.getCurrentUser().getId();

        cartItemRepository.deleteOutOfStockItems(userId);
    }

    @Transactional
    @Override
    public void clearCart() {
        Long userId = authenticationService.getCurrentUser().getId();
        cartItemRepository.deleteByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CartItemResponse> searchCartItems(
            Long productId, String keyWord, Integer minQuantity, Integer maxQuantity, Pageable pageable) {
        Long userId = authenticationService.getCurrentUser().getId();

        Page<CartItem> cartItems =
                cartItemRepository.searchCartItems(userId, keyWord, productId, minQuantity, maxQuantity, pageable);

        return cartItems.map(cartItemMapper::toDTO);
    }
}
