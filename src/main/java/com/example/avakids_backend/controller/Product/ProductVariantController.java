package com.example.avakids_backend.controller.Product;

import java.util.List;

import com.example.avakids_backend.util.language.I18n;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.avakids_backend.DTO.ApiResponse;
import com.example.avakids_backend.DTO.ProductVariant.AddProductVariantRequest;
import com.example.avakids_backend.DTO.ProductVariant.ProductVariantResponse;
import com.example.avakids_backend.DTO.ProductVariant.UpdateProductVariantRequest;
import com.example.avakids_backend.service.ProductVariant.ProductVariantService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product Variant Management", description = "APIs for managing e-commerce products")
public class ProductVariantController {
    private final ProductVariantService variantService;
    private final I18n i18n;


    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
            summary = "Create product variant",
            description = "Create a new variant for a product based on selected option values.")
    @PostMapping("/{productId}/variant/create")
    public ResponseEntity<ApiResponse<ProductVariantResponse>> createVariant(
            @PathVariable Long productId, @Valid @RequestBody AddProductVariantRequest dto) {
        ProductVariantResponse response = variantService.createProductVariant(productId, dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<ProductVariantResponse>builder()
                        .message(i18n.t("product.variant.create.success"))
                        .data(response)
                        .build());
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "Update product variant", description = "Update information of an existing product variant.")
    @PutMapping("/{productId}/variant/{variantId}/update")
    public ResponseEntity<ApiResponse<ProductVariantResponse>> updateVariant(
            @PathVariable Long productId,
            @PathVariable Long variantId,
            @Valid @RequestBody UpdateProductVariantRequest request) {
        ProductVariantResponse response = variantService.updateProductVariant(productId, variantId, request);
        return ResponseEntity.ok(ApiResponse.<ProductVariantResponse>builder()
                .message(i18n.t("product.variant.update.success"))
                .data(response)
                .build());
    }

    @Operation(
            summary = "Get variant detail",
            description = "Retrieve detail information of a specific product variant.")
    @GetMapping("/{productId}/variant/{variantId}/detail")
    public ResponseEntity<ApiResponse<ProductVariantResponse>> getById(
            @PathVariable Long productId, @PathVariable Long variantId) {
        ProductVariantResponse response = variantService.getVariantById(productId, variantId);
        return ResponseEntity.ok(ApiResponse.<ProductVariantResponse>builder()
                .message(i18n.t("product.variant.detail.success"))
                .data(response)
                .build());
    }

    @Operation(summary = "Get product variants", description = "Retrieve all variants of a specific product.")
    @GetMapping("/{productId}/variant/list")
    public ResponseEntity<ApiResponse<List<ProductVariantResponse>>> getAll(@PathVariable Long productId) {
        List<ProductVariantResponse> responses = variantService.getVariantsByProduct(productId);
        return ResponseEntity.ok(ApiResponse.<List<ProductVariantResponse>>builder()
                .message(i18n.t("product.variant.list.success"))
                .data(responses)
                .build());
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "Delete product variant", description = "Delete a specific product variant.")
    @DeleteMapping("/{productId}/variant/{variantId}/delete")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long productId, @PathVariable Long variantId) {
        variantService.deleteProductVariant(productId, variantId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message(i18n.t("product.variant.delete.success"))
                .build());
    }

    @Operation(
            summary = "Select variant by option values",
            description = "Find a product variant based on selected option value IDs.")
    @GetMapping("/{productId}/select-variant-value")
    public ResponseEntity<ApiResponse<ProductVariantResponse>> selectVariant(
            @PathVariable Long productId, @RequestParam(required = false) List<Long> optionValueIds) {
        ProductVariantResponse response = variantService.findVariantByOptions(productId, optionValueIds);

        return ResponseEntity.ok(ApiResponse.<ProductVariantResponse>builder()
                .message(i18n.t("product.variant.select.option.success"))
                .data(response)
                .build());
    }

    @Operation(summary = "Select variant by SKU", description = "Find a product variant by SKU.")
    @GetMapping("/{productId}/select-variant")
    public ResponseEntity<ApiResponse<ProductVariantResponse>> selectVariantBySku(
            @PathVariable Long productId, @RequestParam(required = false) String sku) {
        ProductVariantResponse response = variantService.getVariantBySku(productId, sku);

        return ResponseEntity.ok(ApiResponse.<ProductVariantResponse>builder()
                .message(i18n.t("product.variant.select.sku.success"))
                .data(response)
                .build());
    }
}
