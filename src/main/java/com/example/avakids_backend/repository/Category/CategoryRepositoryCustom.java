package com.example.avakids_backend.repository.Category;

import java.util.List;

import com.example.avakids_backend.entity.Category;

public interface CategoryRepositoryCustom {
    List<Category> findRootCategories();

    List<Category> findChildrenByParentIds(List<Long> parentIds);
}
