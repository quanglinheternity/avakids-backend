package com.example.avakids_backend.service.ProductVariant;

import java.util.List;

import com.example.avakids_backend.DTO.ProductVariant.AddProductVariantRequest;
import com.example.avakids_backend.DTO.ProductVariant.ProductVariantDetailResponse;
import com.example.avakids_backend.DTO.ProductVariant.ProductVariantResponse;
import com.example.avakids_backend.DTO.ProductVariant.UpdateProductVariantRequest;

public interface ProductVariantService {
    ProductVariantResponse createProductVariant(Long productId, AddProductVariantRequest dto);

    ProductVariantResponse updateProductVariant(Long productId, Long variantId, UpdateProductVariantRequest dto);

    ProductVariantResponse getVariantById(Long productId, Long variantId);

    List<ProductVariantResponse> getVariantsByProduct(Long productId);

    void deleteProductVariant(Long productId, Long variantId);

    ProductVariantDetailResponse findVariantByOptions(Long productId, List<Long> optionValueIds);

    ProductVariantDetailResponse getVariantBySku(Long productId, String sku);
}
