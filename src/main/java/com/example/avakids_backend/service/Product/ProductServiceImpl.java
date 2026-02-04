package com.example.avakids_backend.service.Product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.avakids_backend.DTO.Product.ProductCreateRequest;
import com.example.avakids_backend.DTO.Product.ProductResponse;
import com.example.avakids_backend.DTO.Product.ProductSearchRequest;
import com.example.avakids_backend.DTO.Product.ProductUpdateRequest;
import com.example.avakids_backend.entity.Category;
import com.example.avakids_backend.entity.Product;
import com.example.avakids_backend.mapper.ProductMapper;
import com.example.avakids_backend.repository.Product.ProductRepository;
import com.example.avakids_backend.service.Category.CategoryValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryValidator categoryValidator;
    private final ProductValidator productValidator;
    private final ProductMapper productMapper;
    private final ProductRecommendationService productRecommendationService;

    @Override
    public ProductResponse getById(Long id) {
        Product product = productValidator.getProductById(id);
        return productMapper.toResponse(product);
    }

    @Override
    public ProductResponse create(ProductCreateRequest request) {

        productValidator.validateCreate(
                request.getSku(), request.getSlug(), request.getPrice(), request.getSalePrice());

        Category category = categoryValidator.getCategoryById(request.getCategoryId());

        Product product = productMapper.toEntity(request);
        product.setCategory(category);

        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse update(Long id, ProductUpdateRequest request) {
        productValidator.validateUpdate(
                request.getSku(), request.getSlug(), request.getPrice(), request.getSalePrice(), id);
        Product product = productValidator.getProductById(id);

        if (request.getCategoryId() != null) {
            Category category = categoryValidator.getCategoryById(request.getCategoryId());
            product.setCategory(category);
        }

        productMapper.updateEntity(product, request);

        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    public void delete(Long id) {
        Product product = productValidator.getProductById(id);
        productRepository.delete(product);
    }

    @Override
    public Page<ProductResponse> searchProductsForUser(ProductSearchRequest criteria, Pageable pageable) {
        criteria.setAdminSearch(false);
        criteria.setIsActive(true);

        return productRepository.searchProducts(criteria, pageable);
    }

    @Override
    public ProductResponse getProductDetailBySlug(String slug) {
        return productMapper.toResponse(productValidator.findBySlugWithDetails(slug));
    }

    @Override
    public List<ProductResponse> getFeaturedProducts(int limit) {
        return productRepository.findFeaturedProducts(limit).stream().toList();
    }

    @Override
    public List<ProductResponse> getBestSellingProducts(int limit) {
        return productRepository.findBestSellingProducts(limit).stream().toList();
    }

    @Override
    public List<ProductResponse> getNewProducts(int limit) {
        return productRepository.findNewProducts(limit);
    }

    @Override
    public List<ProductResponse> getRelatedProducts(Long productId, Long categoryId, int limit) {
        return productRepository.findRelatedProducts(productId, categoryId, limit);
    }

    @Override
    public Page<ProductResponse> searchProductsForAdmin(ProductSearchRequest criteria, Pageable pageable) {
        criteria.setAdminSearch(true);
        return productRepository.searchProducts(criteria, pageable);
    }

    @Override
    public List<ProductResponse> recommendProducts(Long customerId, Long currentProductId, int limit) {
        return productRecommendationService.recommendProducts(customerId, currentProductId, limit);
    }
}
