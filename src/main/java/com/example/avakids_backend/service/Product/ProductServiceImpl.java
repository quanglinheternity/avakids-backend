package com.example.avakids_backend.service.Product;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.avakids_backend.DTO.Product.ProductCreateRequest;
import com.example.avakids_backend.DTO.Product.ProductResponse;
import com.example.avakids_backend.DTO.Product.ProductUpdateRequest;
import com.example.avakids_backend.Entity.Category;
import com.example.avakids_backend.Entity.Product;
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

    @Override
    public List<ProductResponse> getAll() {
        return productRepository.findAll().stream()
                .map(productMapper::toResponse)
                .toList();
    }

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
}
