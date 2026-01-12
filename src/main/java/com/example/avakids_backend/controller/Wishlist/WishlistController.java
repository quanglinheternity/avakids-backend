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

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/wishlists")
@RequiredArgsConstructor
@Tag(name = "Wishlist Management", description = "APIs for managing  user wishlists")
public class WishlistController {
    private final WishlistService wishlistService;

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

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<Void>> removeFromWishlistByProduct(
            @RequestBody @Valid WishlistCreateRequest request) {

        wishlistService.removeFromWishlist(request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.<Void>builder()
                        .message("Xóa sản phẩm thành công.")
                        .build());
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<Page<WishlistResponse>>> getAllOrders(
            WishlistSearchRequest request,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
                    Pageable pageable) {
        return ResponseEntity.ok()
                .body(ApiResponse.<Page<WishlistResponse>>builder()
                        .message("Lấy danh sách sản phẩm yêu thích thành công.")
                        .data(wishlistService.searchWishlists(request, pageable))
                        .build());
    }
}
