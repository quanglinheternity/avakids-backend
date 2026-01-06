package com.example.avakids_backend.service.Product;

import java.util.List;

import com.example.avakids_backend.DTO.Product.ProductCreateRequest;
import com.example.avakids_backend.DTO.Product.ProductResponse;
import com.example.avakids_backend.DTO.Product.ProductUpdateRequest;

public interface ProductService {

    List<ProductResponse> getAll();

    ProductResponse getById(Long id);

    ProductResponse create(ProductCreateRequest request);

    ProductResponse update(Long id, ProductUpdateRequest request);

    void delete(Long id);
}
