package com.example.avakids_backend.controller.Wishlist;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.avakids_backend.DTO.ApiResponse;
import com.example.avakids_backend.DTO.Wishlist.WishlistCreateRequest;
import com.example.avakids_backend.DTO.Wishlist.WishlistResponse;
import com.example.avakids_backend.DTO.Wishlist.WishlistSearchRequest;
import com.example.avakids_backend.service.Wishlist.WishlistService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/wishlists")
@RequiredArgsConstructor
@Tag(name = "Wishlist Management", description = "APIs for managing user wishlists and favorite products")
public class WishlistController {
    private final WishlistService wishlistService;

    @Operation(summary = "Add product to wishlist", description = "Add a product to the user's wishlist/favorites")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<WishlistResponse>> addToWishlist(
            @RequestBody @Valid WishlistCreateRequest request) {

        WishlistResponse response = wishlistService.addToWishlist(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<WishlistResponse>builder()
                        .message("Thêm sản phẩm thành công")
                        .data(response)
                        .build());
    }

    @Operation(
            summary = "Toggle product in wishlist",
            description =
                    "Toggle add/remove product from wishlist (if already in wishlist, remove it; otherwise add it)")
    @PostMapping("/wishlist/toggle")
    public ResponseEntity<ApiResponse<WishlistResponse>> toggleWishlist(
            @RequestBody @Valid WishlistCreateRequest request) {

        WishlistResponse response = wishlistService.toggleWishlist(request);
        boolean isAdded = response != null;
        return ResponseEntity.status(isAdded ? HttpStatus.CREATED : HttpStatus.OK)
                .body(ApiResponse.<WishlistResponse>builder()
                        .message(
                                isAdded
                                        ? "Thêm sản phẩm vào danh sách yêu thích thành công"
                                        : "Đã xóa sản phẩm khỏi danh sách yêu thích")
                        .data(response)
                        .build());
    }

    @Operation(
            summary = "Remove product from wishlist",
            description = "Remove a specific product from the user's wishlist")
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<Void>> removeFromWishlistByProduct(
            @RequestBody @Valid WishlistCreateRequest request) {

        wishlistService.removeFromWishlist(request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.<Void>builder()
                        .message("Xóa sản phẩm thành công.")
                        .build());
    }

    @Operation(
            summary = "Get wishlist items",
            description = "Retrieve paginated list of products in user's wishlist with search and filter capabilities")
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<Page<WishlistResponse>>> getAllOrders(
            WishlistSearchRequest request,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok()
                .body(ApiResponse.<Page<WishlistResponse>>builder()
                        .message("Lấy danh sách sản phẩm yêu thích thành công.")
                        .data(wishlistService.searchWishlists(request, pageable))
                        .build());
    }
}
