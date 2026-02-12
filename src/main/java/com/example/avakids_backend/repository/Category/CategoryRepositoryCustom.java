package com.example.avakids_backend.repository.Category;

import com.example.avakids_backend.entity.Category;

import java.util.List;

public interface CategoryRepositoryCustom {
    List<Category> findRootCategories();
    List<Category> findChildrenByParentIds(List<Long> parentIds);
}
