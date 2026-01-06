package com.example.avakids_backend.service.Category;

import org.springframework.stereotype.Component;

import com.example.avakids_backend.Entity.Category;
import com.example.avakids_backend.exception.AppException;
import com.example.avakids_backend.exception.ErrorCode;
import com.example.avakids_backend.repository.Category.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CategoryValidator {
    private final CategoryRepository categoryRepository;

    public void validateCreateUser(String Name, String Slug) {
        validateExistsByName(Name);
        validateExistsBySlug(Slug);
    }

    public void validateUpdateCategory(String Name, String Slug, Long id) {
        validateExistsByNameAndIdNot(Name, id);
        validateExistsBySlugAndIdNot(Slug, id);
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    private void validateExistsByName(String Name) {
        if (categoryRepository.existsByName(Name)) {
            throw new AppException(ErrorCode.CATEGORY_NAME_ALREADY_EXISTS);
        }
    }

    private void validateExistsBySlug(String Slug) {
        if (categoryRepository.existsBySlug(Slug)) {
            throw new AppException(ErrorCode.CATEGORY_SLUG_ALREADY_EXISTS);
        }
    }

    private void validateExistsByNameAndIdNot(String Name, Long id) {
        if (categoryRepository.existsByNameAndIdNot(Name, id)) {
            throw new AppException(ErrorCode.CATEGORY_NAME_ALREADY_EXISTS);
        }
    }

    private void validateExistsBySlugAndIdNot(String Slug, Long id) {
        if (categoryRepository.existsBySlugAndIdNot(Slug, id)) {
            throw new AppException(ErrorCode.CATEGORY_SLUG_ALREADY_EXISTS);
        }
    }
}
