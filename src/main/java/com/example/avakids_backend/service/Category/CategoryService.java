package com.example.avakids_backend.service.Category;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.avakids_backend.DTO.Category.CategoryCreateRequest;
import com.example.avakids_backend.DTO.Category.CategoryResponse;
import com.example.avakids_backend.DTO.Category.CategoryUpdateRequest;

public interface CategoryService {

    List<CategoryResponse> getAll();

    CategoryResponse create(CategoryCreateRequest request, MultipartFile file);

    CategoryResponse update(Long id, CategoryUpdateRequest request, MultipartFile file);

    void delete(Long id);
}
