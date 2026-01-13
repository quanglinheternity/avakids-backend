package com.example.avakids_backend.controller.Product;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.avakids_backend.DTO.ApiResponse;
import com.example.avakids_backend.DTO.Product.ProductCreateRequest;
import com.example.avakids_backend.DTO.Product.ProductResponse;
import com.example.avakids_backend.DTO.Product.ProductSearchRequest;
import com.example.avakids_backend.DTO.Product.ProductUpdateRequest;
import com.example.avakids_backend.service.Product.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "product", description = "APIs for managing product")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Get all product with pagination")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> searchProducts(
            ProductSearchRequest request,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
                    Pageable pageable) {

        Page<ProductResponse> products = productService.searchProductsForUser(request, pageable);

        return ResponseEntity.ok()
                .body(ApiResponse.<Page<ProductResponse>>builder()
                        .message("Lấy sản phẩm nhận hàng thành công")
                        .data(products)
                        .build());
    }

    @Operation(summary = "Get all product with pagination")
    @GetMapping("/admin/products")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> searchProductsForAdmin(
            ProductSearchRequest request,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
                    Pageable pageable) {

        Page<ProductResponse> products = productService.searchProductsForAdmin(request, pageable);

        return ResponseEntity.ok()
                .body(ApiResponse.<Page<ProductResponse>>builder()
                        .message("Lấy sản phẩm nhận hàng thành công")
                        .data(products)
                        .build());
    }

    @Operation(summary = "Create or a new Product")
    @PostMapping("/create")
    public ApiResponse<ProductResponse> create(@RequestBody @Valid ProductCreateRequest request) {

        return ApiResponse.<ProductResponse>builder()
                .message("Tạo địa chỉ nhận hàng thành công")
                .data(productService.create(request))
                .build();
    }

    @Operation(summary = "Update a Product by ID")
    @PutMapping("/{id}/update")
    public ApiResponse<ProductResponse> update(
            @PathVariable Long id, @RequestBody @Valid ProductUpdateRequest request) {
        return ApiResponse.<ProductResponse>builder()
                .message("Cập nhật sản phẩm thành công")
                .data(productService.update(id, request))
                .build();
    }

    @Operation(summary = "Delete a Product by ID")
    @DeleteMapping("/{id}/delete")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ApiResponse.<Void>builder().message("Xóa sản phẩm thành công").build();
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductBySlug(@PathVariable String slug) {
        ProductResponse product = productService.getProductDetailBySlug(slug);
        return ResponseEntity.ok()
                .body(ApiResponse.<ProductResponse>builder()
                        .message("Lấy sản phẩm thành công")
                        .data(product)
                        .build());
    }

    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getFeaturedProducts(
            @RequestParam(defaultValue = "8") int limit) {
        List<ProductResponse> products = productService.getFeaturedProducts(limit);
        return ResponseEntity.ok()
                .body(ApiResponse.<List<ProductResponse>>builder()
                        .message("Lấy sản phẩm top nổi bật thành công")
                        .data(products)
                        .build());
    }

    @GetMapping("/best-selling")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getBestSellingProducts(
            @RequestParam(defaultValue = "8") int limit) {
        List<ProductResponse> products = productService.getBestSellingProducts(limit);
        return ResponseEntity.ok()
                .body(ApiResponse.<List<ProductResponse>>builder()
                        .message("Lấy sản phẩm top sell thành công")
                        .data(products)
                        .build());
    }

    @GetMapping("/new")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getNewProducts(
            @RequestParam(defaultValue = "8") int limit) {
        List<ProductResponse> products = productService.getNewProducts(limit);
        return ResponseEntity.ok()
                .body(ApiResponse.<List<ProductResponse>>builder()
                        .message("Lấy sản phẩm mới nhất thành công")
                        .data(products)
                        .build());
    }

    @GetMapping("/{id}/related")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getNewProducts(
            @PathVariable Long id, @RequestParam Long categoryId, @RequestParam(defaultValue = "4") int limit) {
        List<ProductResponse> products = productService.getRelatedProducts(id, categoryId, limit);
        return ResponseEntity.ok()
                .body(ApiResponse.<List<ProductResponse>>builder()
                        .message("Lấy sản phẩm cùng danh mục thành công")
                        .data(products)
                        .build());
    }
}
