package com.example.avakids_backend.controller.Product;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.avakids_backend.DTO.ApiResponse;
import com.example.avakids_backend.DTO.Product.ProductCreateRequest;
import com.example.avakids_backend.DTO.Product.ProductResponse;
import com.example.avakids_backend.DTO.Product.ProductSearchRequest;
import com.example.avakids_backend.DTO.Product.ProductUpdateRequest;
import com.example.avakids_backend.service.Product.ProductService;
import com.example.avakids_backend.util.language.I18n;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product Management", description = "APIs for managing e-commerce products")
public class ProductController {

    private final ProductService productService;
    private final I18n i18n;

    @Operation(
            summary = "Search products for users",
            description =
                    "Search and filter products with pagination for regular users (excludes unpublished products)")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> searchProducts(
            ProductSearchRequest request,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ProductResponse> products = productService.searchProductsForUser(request, pageable);

        return ResponseEntity.ok()
                .body(ApiResponse.<Page<ProductResponse>>builder()
                        .message(i18n.t("product.search.user.success"))
                        .data(products)
                        .build());
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            summary = "Search products for admin",
            description =
                    "Search and filter products with pagination for administrators (includes all products including unpublished)")
    @GetMapping("/admin/products")
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> searchProductsForAdmin(
            ProductSearchRequest request,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ProductResponse> products = productService.searchProductsForAdmin(request, pageable);

        return ResponseEntity.ok()
                .body(ApiResponse.<Page<ProductResponse>>builder()
                        .message(i18n.t("product.search.admin.success"))
                        .data(products)
                        .build());
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            summary = "Create a new product",
            description = "Create a new product with details like name, description, price, images, and inventory")
    @PostMapping("/create")
    public ApiResponse<ProductResponse> create(@RequestBody @Valid ProductCreateRequest request) {

        return ApiResponse.<ProductResponse>builder()
                .message(i18n.t("product.create.success"))
                .data(productService.create(request))
                .build();
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            summary = "Update a product by ID",
            description = "Update existing product information including price, description, inventory, etc.")
    @PutMapping("/{id}/update")
    public ApiResponse<ProductResponse> update(
            @PathVariable Long id, @RequestBody @Valid ProductUpdateRequest request) {
        return ApiResponse.<ProductResponse>builder()
                .message(i18n.t("product.update.success"))
                .data(productService.update(id, request))
                .build();
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            summary = "Delete a product by ID",
            description = "Soft delete or permanently remove a product from the system")
    @DeleteMapping("/{id}/delete")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ApiResponse.<Void>builder()
                .message(i18n.t("product.delete.success"))
                .build();
    }

    @Operation(summary = "Get product by slug", description = "Retrieve product details using SEO-friendly slug URL")
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductBySlug(@PathVariable String slug) {
        ProductResponse product = productService.getProductDetailBySlug(slug);
        return ResponseEntity.ok()
                .body(ApiResponse.<ProductResponse>builder()
                        .message(i18n.t("product.get.detail.success"))
                        .data(product)
                        .build());
    }

    @Operation(
            summary = "Get featured products",
            description = "Retrieve a list of featured products (manually selected by admin)")
    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getFeaturedProducts(
            @RequestParam(defaultValue = "8") int limit) {
        List<ProductResponse> products = productService.getFeaturedProducts(limit);
        return ResponseEntity.ok()
                .body(ApiResponse.<List<ProductResponse>>builder()
                        .message(i18n.t("product.featured.success"))
                        .data(products)
                        .build());
    }

    @Operation(
            summary = "Get best-selling products",
            description = "Retrieve top-selling products based on sales volume")
    @GetMapping("/best-selling")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getBestSellingProducts(
            @RequestParam(defaultValue = "8") int limit) {
        List<ProductResponse> products = productService.getBestSellingProducts(limit);
        return ResponseEntity.ok()
                .body(ApiResponse.<List<ProductResponse>>builder()
                        .message(i18n.t("product.best_selling.success"))
                        .data(products)
                        .build());
    }

    @Operation(summary = "Get new products", description = "Retrieve recently added products (sorted by creation date)")
    @GetMapping("/new")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getNewProducts(
            @RequestParam(defaultValue = "8") int limit) {
        List<ProductResponse> products = productService.getNewProducts(limit);
        return ResponseEntity.ok()
                .body(ApiResponse.<List<ProductResponse>>builder()
                        .message(i18n.t("product.new.success"))
                        .data(products)
                        .build());
    }

    @Operation(
            summary = "Get related products",
            description = "Retrieve products related to a specific product (usually from same category)")
    @GetMapping("/{id}/related")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getNewProducts(
            @PathVariable Long id, @RequestParam Long categoryId, @RequestParam(defaultValue = "4") int limit) {
        List<ProductResponse> products = productService.getRelatedProducts(id, categoryId, limit);
        return ResponseEntity.ok()
                .body(ApiResponse.<List<ProductResponse>>builder()
                        .message(i18n.t("product.related.success"))
                        .data(products)
                        .build());
    }

    @Operation(
            summary = "Recommend products",
            description =
                    "Retrieve a list of recommended products based on user behavior or the currently viewed product.")
    @GetMapping("/recommend")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> recommendProducts(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Long currentVariantId,
            @RequestParam(defaultValue = "10") int limit) {
        List<ProductResponse> variants = productService.recommendProducts(customerId, currentVariantId, limit);

        return ResponseEntity.ok()
                .body(ApiResponse.<List<ProductResponse>>builder()
                        .message(i18n.t("product.recommend.success"))
                        .data(variants)
                        .build());
    }
}
