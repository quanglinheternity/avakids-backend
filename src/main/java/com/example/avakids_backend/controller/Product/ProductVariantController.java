package com.example.avakids_backend.controller.Product;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.avakids_backend.DTO.ProductVariant.AddProductVariantRequest;
import com.example.avakids_backend.DTO.ProductVariant.ProductVariantResponse;
import com.example.avakids_backend.DTO.ProductVariant.UpdateProductVariantRequest;
import com.example.avakids_backend.service.ProductVariant.ProductVariantService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product Management", description = "APIs for managing e-commerce products")
public class ProductVariantController {
    private final ProductVariantService variantService;

    @PostMapping("/{productId}/variant/create")
    public ResponseEntity<ProductVariantResponse> createVariant(
            @PathVariable Long productId, @Valid @RequestBody AddProductVariantRequest dto) {
        ProductVariantResponse response = variantService.createProductVariant(productId, dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{productId}/variant/{variantId}/update")
    public ResponseEntity<ProductVariantResponse> updateVariant(
            @PathVariable Long productId,
            @PathVariable Long variantId,
            @Valid @RequestBody UpdateProductVariantRequest request) {
        return ResponseEntity.ok(variantService.updateProductVariant(productId, variantId, request));
    }

    @GetMapping("/{productId}/variant/{variantId}/detail")
    public ResponseEntity<ProductVariantResponse> getById(@PathVariable Long productId, @PathVariable Long variantId) {
        return ResponseEntity.ok(variantService.getVariantById(productId, variantId));
    }

    @GetMapping("/{productId}/variant/list")
    public ResponseEntity<List<ProductVariantResponse>> getAll(@PathVariable Long productId) {
        return ResponseEntity.ok(variantService.getVariantsByProduct(productId));
    }

    @DeleteMapping("/{productId}/variant/{variantId}/delete")
    public ResponseEntity<Void> delete(@PathVariable Long productId, @PathVariable Long variantId) {
        variantService.deleteProductVariant(productId, variantId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{productId}/select-variant-value")
    public ProductVariantResponse selectVariant(
            @PathVariable Long productId, @RequestParam(required = false) List<Long> optionValueIds) {
        return variantService.findVariantByOptions(productId, optionValueIds);
    }

    @GetMapping("/{productId}/select-variant")
    public ProductVariantResponse selectVariantBySku(
            @PathVariable Long productId, @RequestParam(required = false) String sku) {
        return variantService.getVariantBySku(productId, sku);
    }
}
