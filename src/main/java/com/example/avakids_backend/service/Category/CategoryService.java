package com.example.avakids_backend.service.Category;

import java.util.List;

import com.example.avakids_backend.DTO.Category.CategoryCreateRequest;
import com.example.avakids_backend.DTO.Category.CategoryResponse;
import com.example.avakids_backend.DTO.Category.CategoryUpdateRequest;

public interface CategoryService {

    List<CategoryResponse> getAll();

    CategoryResponse create(CategoryCreateRequest request);

    CategoryResponse update(Long id, CategoryUpdateRequest request);

    void delete(Long id);
}
