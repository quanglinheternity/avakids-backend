package com.example.avakids_backend.repository.Category;

import com.example.avakids_backend.entity.Category;
import com.example.avakids_backend.entity.QCategory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    QCategory category = QCategory.category;

    @Override
    public List<Category> findRootCategories() {
        return queryFactory
                .selectFrom(category)
                .where(category.parent.isNull())
                .fetch();
    }

    @Override
    public List<Category> findChildrenByParentIds(List<Long> parentIds) {
        return queryFactory
                .selectFrom(category)
                .where(category.parent.id.in(parentIds))
                .fetch();
    }
}
