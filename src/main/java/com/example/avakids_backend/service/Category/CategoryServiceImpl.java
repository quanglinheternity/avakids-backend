package com.example.avakids_backend.service.Category;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.avakids_backend.DTO.Category.CategoryCreateRequest;
import com.example.avakids_backend.DTO.Category.CategoryResponse;
import com.example.avakids_backend.DTO.Category.CategoryUpdateRequest;
import com.example.avakids_backend.entity.Category;
import com.example.avakids_backend.mapper.CategoryMapper;
import com.example.avakids_backend.repository.Category.CategoryRepository;
import com.example.avakids_backend.util.file.sevrice.CloudService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final CategoryValidator categoryValidator;
    private final CloudService fileStorageService;
    private static final String CATEGORY_IMAGE_FOLDER = "category";

    @Override
    public List<CategoryResponse> getAll() {

        List<Category> roots = categoryRepository.findRootCategories();

        if (roots.isEmpty()) {
            return List.of();
        }

        List<Long> rootIds = roots.stream()
                .map(Category::getId)
                .toList();

        List<Category> children = categoryRepository.findChildrenByParentIds(rootIds);

        Map<Long, List<Category>> childrenMap =
                children.stream()
                        .collect(Collectors.groupingBy(c -> c.getParent().getId()));

        return roots.stream()
                .map(root -> {
                    List<Category> childList =
                            childrenMap.getOrDefault(root.getId(), List.of());

                    return categoryMapper.toResponseWithChildren(root, childList);
                })
                .toList();
    }

    public CategoryResponse create(CategoryCreateRequest request, MultipartFile file) {
        categoryValidator.validateCreateUser(request.getName(), request.getSlug());
        fileStorageService.validateImage(file);
        Category parent = parentCategory(request.getParentId());
        String imageUrl = fileStorageService.uploadFile(file, CATEGORY_IMAGE_FOLDER);

        Category category = categoryMapper.toEntity(request);
        category.setImageUrl(imageUrl);
        category.setParent(parent);
        category = categoryRepository.save(category);

        return categoryMapper.toResponse(category);
    }

    public CategoryResponse update(Long id, CategoryUpdateRequest request, MultipartFile file) {
        categoryValidator.validateUpdateCategory(request.getName(), request.getSlug(), id);

        Category category = categoryValidator.getCategoryById(id);

        Category parent = parentCategory(request.getParentId());

        categoryMapper.updateEntityFromDTO(request, category);
        category.setParent(parent);
        if (file != null && !file.isEmpty()) {
            fileStorageService.validateImage(file);
            fileStorageService.deleteFile(category.getImageUrl());
            String imageUrl = fileStorageService.uploadFile(file, CATEGORY_IMAGE_FOLDER);
            category.setImageUrl(imageUrl);
        }

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
