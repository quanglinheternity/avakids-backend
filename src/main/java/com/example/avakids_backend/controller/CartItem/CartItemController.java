package com.example.avakids_backend.controller.CartItem;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.avakids_backend.DTO.ApiResponse;
import com.example.avakids_backend.DTO.CartItem.AddToCartRequest;
import com.example.avakids_backend.DTO.CartItem.CartItemResponse;
import com.example.avakids_backend.DTO.CartItem.CartSummaryResponse;
import com.example.avakids_backend.DTO.CartItem.UpdateCartItemRequest;
import com.example.avakids_backend.service.CartItem.CartItemService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/cart-items")
@RequiredArgsConstructor
public class CartItemController {
    private final CartItemService cartItemService;

    @PostMapping("/addItems")
    public ResponseEntity<ApiResponse<CartItemResponse>> addToCart(@Valid @RequestBody AddToCartRequest request) {

        CartItemResponse cartItem = cartItemService.addToCart(request.getProductId(), request.getQuantity());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<CartItemResponse>builder()
                        .message("Sản phẩm được thêm vào giỏ hàng.")
                        .data(cartItem)
                        .build());
    }

    @PutMapping("/updateCart/{cartItemId}")
    public ResponseEntity<ApiResponse<CartItemResponse>> updateCartItemQuantity(
            @PathVariable Long cartItemId, @Valid @RequestBody UpdateCartItemRequest request) {

        CartItemResponse cartItem = cartItemService.updateCartItemQuantity(cartItemId, request.getQuantity());

        return ResponseEntity.ok(ApiResponse.<CartItemResponse>builder()
                .message("Cập nhật số lượng thành công.")
                .data(cartItem)
                .build());
    }

    @DeleteMapping("/deleteItems/{cartItemId}")
    public ResponseEntity<ApiResponse<Void>> removeFromCart(@PathVariable Long cartItemId) {
        cartItemService.removeFromCart(cartItemId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder().message("Xóa sản phẩm thành công.").build());
    }

    @GetMapping("/getAllItems")
    public ResponseEntity<ApiResponse<List<CartItemResponse>>> getCartItems() {

        List<CartItemResponse> cartItems = cartItemService.getCartItems();

        return ResponseEntity.ok(ApiResponse.<List<CartItemResponse>>builder()
                .message("Lấy tất cả sản phẩm thành công.")
                .data(cartItems)
                .build());
    }

    @GetMapping("/getAllSummary")
    public ResponseEntity<ApiResponse<CartSummaryResponse>> getCartSummary() {

        CartSummaryResponse summary = cartItemService.getCartSummary();

        return ResponseEntity.ok(ApiResponse.<CartSummaryResponse>builder()
                .message("Lấy thông tin giỏ hàng thành công.")
                .data(summary)
                .build());
    }

    @DeleteMapping("/remove-out-of-stock")
    public ResponseEntity<ApiResponse<Void>> removeOutOfStockItems() {

        cartItemService.removeOutOfStockItems();

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Xóa tất cả sảm phẩm (hết hàng) ra giỏ hàng thành công")
                .build());
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<Void>> clearCart() {

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Xóa tất cả sảm phẩm ra giỏ hàng thành công")
                .build());
    }

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
                .message("Lấy sản phẩm theo tìm kiếm thành công.")
                .data(cartItems)
                .build());
    }
}
