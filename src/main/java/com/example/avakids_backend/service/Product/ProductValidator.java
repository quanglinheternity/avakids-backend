package com.example.avakids_backend.service.Product;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.example.avakids_backend.entity.Product;
import com.example.avakids_backend.exception.AppException;
import com.example.avakids_backend.exception.ErrorCode;
import com.example.avakids_backend.repository.Product.ProductRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductValidator {
    private final ProductRepository productRepository;

    public void validateCreate(String sku, String slug, BigDecimal price, BigDecimal salePrice) {
        existsBySku(sku);
        existsBySlug(slug);
        validateSalePrice(price, salePrice);
    }

    public void validateUpdate(String sku, String slug, BigDecimal price, BigDecimal salePrice, Long id) {
        existsBySkuAndIdNot(sku, id);
        existsBySlugAndIdNot(slug, id);
        validateSalePrice(price, salePrice);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    public Product getProductByIdAndIsActive(Long id) {
        Product product = getProductById(id);
        if (!product.getIsActive()) {
            throw new AppException(ErrorCode.PRODUCT_IS_ACTIVE);
        }
        return product;
    }

    private void existsBySkuAndIdNot(String sku, Long id) {
        if (productRepository.existsBySkuAndIdNot(sku, id)) {
            throw new AppException(ErrorCode.PRODUCT_SKU_ALREADY_EXISTS);
        }
    }

    private void existsBySlugAndIdNot(String slug, Long id) {
        if (productRepository.existsBySlugAndIdNot(slug, id)) {
            throw new AppException(ErrorCode.PRODUCT_SLUG_ALREADY_EXISTS);
        }
    }

    private void existsBySku(String sku) {
        if (productRepository.existsBySku(sku)) {
            throw new AppException(ErrorCode.PRODUCT_SKU_ALREADY_EXISTS);
        }
    }

    private void existsBySlug(String slug) {
        if (productRepository.existsBySlug(slug)) {
            throw new AppException(ErrorCode.PRODUCT_SLUG_ALREADY_EXISTS);
        }
    }

    private void validateSalePrice(BigDecimal price, BigDecimal salePrice) {
        if (salePrice == null) {
            return;
        }

        if (price == null) {
            return;
        }

        if (salePrice.compareTo(price) > 0) {
            throw new AppException(ErrorCode.PRODUCT_SALE_PRICE_GREATER_THAN_PRICE);
        }
    }
}
