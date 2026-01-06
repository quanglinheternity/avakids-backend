package com.example.avakids_backend.service.Category;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.example.avakids_backend.DTO.Category.CategoryCreateRequest;
import com.example.avakids_backend.DTO.Category.CategoryResponse;
import com.example.avakids_backend.DTO.Category.CategoryUpdateRequest;
import com.example.avakids_backend.Entity.Category;
import com.example.avakids_backend.mapper.CategoryMapper;
import com.example.avakids_backend.repository.Category.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final CategoryValidator categoryValidator;

    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    public CategoryResponse create(CategoryCreateRequest request) {
        categoryValidator.validateCreateUser(request.getName(), request.getSlug());
        Category parent = parentCategory(request.getParentId());

        Category category = categoryMapper.toEntity(request);
        category.setParent(parent);
        category = categoryRepository.save(category);

        return categoryMapper.toResponse(category);
    }

    public CategoryResponse update(Long id, CategoryUpdateRequest request) {
        categoryValidator.validateUpdateCategory(request.getName(), request.getSlug(), id);

        Category category = categoryValidator.getCategoryById(id);

        Category parent = parentCategory(request.getParentId());

        categoryMapper.updateEntityFromDTO(request, category);
        category.setParent(parent);

        category = categoryRepository.save(category);
        return categoryMapper.toResponse(category);
    }

    public void delete(Long id) {
        Category category = categoryValidator.getCategoryById(id);
        categoryRepository.delete(category);
    }

    private Category parentCategory(Long id) {
        Category parent = null;
        if (id != null) {
            parent = categoryValidator.getCategoryById(id);
        }
        return parent;
    }
}
