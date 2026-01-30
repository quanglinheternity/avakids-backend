package com.example.avakids_backend.controller.CartItem;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.avakids_backend.DTO.ApiResponse;
import com.example.avakids_backend.DTO.CartItem.AddToCartRequest;
import com.example.avakids_backend.DTO.CartItem.CartItemResponse;
import com.example.avakids_backend.DTO.CartItem.CartSummaryResponse;
import com.example.avakids_backend.DTO.CartItem.UpdateCartItemRequest;
import com.example.avakids_backend.service.CartItem.CartItemService;
import com.example.avakids_backend.util.language.I18n;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/cart-items")
@RequiredArgsConstructor
@Tag(name = "Cart Item Management", description = "APIs for managing shopping cart items")
public class CartItemController {
    private final CartItemService cartItemService;
    private final I18n i18n;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Add item to cart", description = "Add a product to the shopping cart with specified quantity")
    @PostMapping("/addItems")
    public ResponseEntity<ApiResponse<CartItemResponse>> addToCart(@Valid @RequestBody AddToCartRequest request) {

        CartItemResponse cartItem = cartItemService.addToCart(request.getProductId(), request.getQuantity());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<CartItemResponse>builder()
                        .message(i18n.t("cart.add.success"))
                        .data(cartItem)
                        .build());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(
            summary = "Update cart item quantity",
            description = "Update the quantity of a specific item in the cart")
    @PutMapping("/updateCart/{cartItemId}")
    public ResponseEntity<ApiResponse<CartItemResponse>> updateCartItemQuantity(
            @PathVariable Long cartItemId, @Valid @RequestBody UpdateCartItemRequest request) {

        CartItemResponse cartItem = cartItemService.updateCartItemQuantity(cartItemId, request.getQuantity());

        return ResponseEntity.ok(ApiResponse.<CartItemResponse>builder()
                .message(i18n.t("cart.update.success"))
                .data(cartItem)
                .build());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Remove item from cart", description = "Remove a specific item from the shopping cart")
    @DeleteMapping("/deleteItems/{cartItemId}")
    public ResponseEntity<ApiResponse<Void>> removeFromCart(@PathVariable Long cartItemId) {
        cartItemService.removeFromCart(cartItemId);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message(i18n.t("cart.remove.success"))
                .build());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Get all cart items", description = "Retrieve all items currently in the shopping cart")
    @GetMapping("/getAllItems")
    public ResponseEntity<ApiResponse<List<CartItemResponse>>> getCartItems() {

        List<CartItemResponse> cartItems = cartItemService.getCartItems();

        return ResponseEntity.ok(ApiResponse.<List<CartItemResponse>>builder()
                .message(i18n.t("cart.get_all.success"))
                .data(cartItems)
                .build());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(
            summary = "Get cart summary",
            description = "Get summary information of the shopping cart including total items, quantities and price")
    @GetMapping("/getAllSummary")
    public ResponseEntity<ApiResponse<CartSummaryResponse>> getCartSummary() {

        CartSummaryResponse summary = cartItemService.getCartSummary();

        return ResponseEntity.ok(ApiResponse.<CartSummaryResponse>builder()
                .message(i18n.t("cart.summary.success"))
                .data(summary)
                .build());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(
            summary = "Remove out of stock items",
            description = "Remove all items that are out of stock from the cart")
    @DeleteMapping("/remove-out-of-stock")
    public ResponseEntity<ApiResponse<Void>> removeOutOfStockItems() {

        cartItemService.removeOutOfStockItems();

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message(i18n.t("cart.remove_out_of_stock.success"))
                .build());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Clear cart", description = "Remove all items from the shopping cart")
    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<Void>> clearCart() {

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message(i18n.t("cart.clear.success"))
                .build());
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            summary = "Search cart items",
            description = "Search and filter cart items with various criteria like product ID, keyword, quantity range")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<CartItemResponse>>> searchCartItems(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) String keyWord,
            @RequestParam(required = false) Integer minQuantity,
            @RequestParam(required = false) Integer maxQuantity,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<CartItemResponse> cartItems =
                cartItemService.searchCartItems(productId, keyWord, minQuantity, maxQuantity, pageable);

        return ResponseEntity.ok(ApiResponse.<Page<CartItemResponse>>builder()
                .message(i18n.t("cart.search.success"))
                .data(cartItems)
                .build());
    }
}
