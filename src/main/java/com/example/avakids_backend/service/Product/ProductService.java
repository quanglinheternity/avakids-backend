package com.example.avakids_backend.service.Product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.avakids_backend.DTO.Product.ProductCreateRequest;
import com.example.avakids_backend.DTO.Product.ProductResponse;
import com.example.avakids_backend.DTO.Product.ProductSearchRequest;
import com.example.avakids_backend.DTO.Product.ProductUpdateRequest;

public interface ProductService {

    ProductResponse getById(Long id);

    ProductResponse create(ProductCreateRequest request);

    ProductResponse update(Long id, ProductUpdateRequest request);

    void delete(Long id);

    Page<ProductResponse> searchProductsForUser(ProductSearchRequest criteria, Pageable pageable);

    ProductResponse getProductDetailBySlug(String slug);

    List<ProductResponse> getFeaturedProducts(int limit);

    List<ProductResponse> getBestSellingProducts(int limit);

    List<ProductResponse> getNewProducts(int limit);

    List<ProductResponse> getRelatedProducts(Long productId, Long categoryId, int limit);

    Page<ProductResponse> searchProductsForAdmin(ProductSearchRequest criteria, Pageable pageable);
}
