package com.example.avakids_backend.repository.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.avakids_backend.DTO.Product.ProductSearchRequest;
import com.example.avakids_backend.entity.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QProduct p = QProduct.product;
    private final QProductVariant v = QProductVariant.productVariant;
    private final QOrderItem oi = QOrderItem.orderItem;

    @Override
    public Page<Product> searchProducts(ProductSearchRequest criteria, Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();

        // Điều kiện tìm kiếm
        if (criteria.getKeyword() != null && !criteria.getKeyword().isEmpty()) {
            builder.and(p.name.containsIgnoreCase(criteria.getKeyword())
                    .or(p.description.containsIgnoreCase(criteria.getKeyword()))
                    .or(p.sku.containsIgnoreCase(criteria.getKeyword())));
        }

        if (criteria.getCategoryId() != null) {
            builder.and(p.category.id.eq(criteria.getCategoryId()));
        }

        if (criteria.getMinPrice() != null) {
            builder.and(p.price.goe(criteria.getMinPrice()));
        }

        if (criteria.getMaxPrice() != null) {
            builder.and(p.price.loe(criteria.getMaxPrice()));
        }

        if (criteria.getIsActive() != null) {
            builder.and(p.isActive.eq(criteria.getIsActive()));
        } else {
            // Mặc định chỉ lấy sản phẩm active cho user
            if (!criteria.isAdminSearch()) {
                builder.and(p.isActive.eq(true));
            }
        }

        if (criteria.getIsFeatured() != null) {
            builder.and(p.isFeatured.eq(criteria.getIsFeatured()));
        }

        if (criteria.getInStock() != null && criteria.getInStock()) {
            builder.and(p.totalStock.gt(0));
        }

        if (criteria.getMinRating() != null) {
            builder.and(p.avgRating.goe(criteria.getMinRating()));
        }

        // Sắp xếp
        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(criteria.getSortBy(), criteria.getSortDirection(), p);

        // Truy vấn với phân trang
        List<Product> products = queryFactory
                .selectFrom(p)
                .leftJoin(p.category)
                .fetchJoin()
                .where(builder)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Đếm tổng số
        Long total = queryFactory.select(p.count()).from(p).where(builder).fetchOne();

        return new PageImpl<>(products, pageable, total != null ? total : 0);
    }

    @Override
    public List<Product> findFeaturedProducts(int limit) {

        return queryFactory
                .selectFrom(p)
                .leftJoin(p.images)
                .fetchJoin()
                .where(p.isActive.eq(true).and(p.isFeatured.eq(true)).and(p.totalStock.gt(0)))
                .orderBy(p.soldCount.desc(), p.avgRating.desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public List<Product> findBestSellingProducts(int limit) {

        return queryFactory
                .selectFrom(p)
                .leftJoin(p.images)
                .fetchJoin()
                .where(p.isActive.eq(true).and(p.soldCount.gt(0)))
                .orderBy(p.soldCount.desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public List<Product> findNewProducts(int limit) {

        return queryFactory
                .selectFrom(p)
                .leftJoin(p.images)
                .fetchJoin()
                .where(p.isActive.eq(true).and(p.totalStock.gt(0)))
                .orderBy(p.createdAt.desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public List<Product> findRelatedProducts(Long productId, Long categoryId, int limit) {

        return queryFactory
                .selectFrom(p)
                .leftJoin(p.images)
                .fetchJoin()
                .where(p.isActive
                        .eq(true)
                        .and(p.totalStock.gt(0))
                        .and(p.id.ne(productId))
                        .and(p.category.id.eq(categoryId)))
                .orderBy(p.soldCount.desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public Optional<Product> findByIdWithImages(Long id) {

        Product result = queryFactory
                .selectFrom(p)
                .leftJoin(p.images)
                .fetchJoin()
                .leftJoin(p.category)
                .fetchJoin()
                .where(p.id.eq(id))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<Product> findBySlugWithDetails(String slug) {

        Product result = queryFactory
                .selectFrom(p)
                .leftJoin(p.images)
                .fetchJoin()
                .leftJoin(p.category)
                .fetchJoin()
                .where(p.slug.eq(slug).and(p.isActive.eq(true)))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    @Transactional
    public void updateProductRating(Long productId, BigDecimal avgRating, Integer reviewCount) {

        queryFactory
                .update(p)
                .set(p.avgRating, avgRating)
                .set(p.reviewCount, reviewCount)
                .set(p.updatedAt, LocalDateTime.now())
                .where(p.id.eq(productId))
                .execute();
    }

    //  Helper: subquery tính tồn kho
    private NumberExpression<Integer> totalStock() {
        JPQLQuery<Integer> subQuery = JPAExpressions.select(
                        v.stockQuantity.sum().coalesce(0))
                .from(v)
                .where(
                        v.product.id.eq(p.id)
                        //                        v.isActive.isTrue()
                        );
        return Expressions.numberTemplate(Integer.class, "({0})", subQuery);
    }

    //      1. Same category
    @Override
    public List<Product> findByCategoryAndExcludeIds(Long categoryId, Set<Long> excludedIds, int minStock, int limit) {
        return queryFactory
                .selectFrom(p)
                .where(
                        p.category.id.eq(categoryId),
                        p.isActive.isTrue(),
                        excludedIds.isEmpty() ? null : p.id.notIn(excludedIds),
                        totalStock().goe(minStock))
                .orderBy(p.updatedAt.desc())
                .limit(limit)
                .fetch();
    }

    //      2. Purchased categories
    @Override
    public List<Product> findByCategoryIdsExcludeIds(
            Set<Long> categoryIds, Set<Long> excludedIds, int minStock, int limit) {
        return queryFactory
                .selectFrom(p)
                .where(
                        p.category.id.in(categoryIds),
                        p.isActive.isTrue(),
                        excludedIds.isEmpty() ? null : p.id.notIn(excludedIds),
                        totalStock().goe(minStock))
                .orderBy(p.updatedAt.desc())
                .limit(limit)
                .fetch();
    }

    //      3. Popular products (best seller)
    @Override
    public List<Product> findPopularExcludeIds(Set<Long> excludedIds, int minStock, int limit) {
        return queryFactory
                .select(p)
                .from(p)
                .leftJoin(oi)
                .on(oi.variant.product.id.eq(p.id))
                .where(
                        p.isActive.isTrue(),
                        excludedIds.isEmpty() ? null : p.id.notIn(excludedIds),
                        totalStock().goe(minStock))
                .groupBy(p.id)
                .orderBy(oi.id.count().desc()) // số lần bán
                .limit(limit)
                .fetch();
    }

    //     4. Fallback: còn hàng
    @Override
    public List<Product> findAnyInStockExcludeIds(Set<Long> excludedIds, int limit) {
        return queryFactory
                .selectFrom(p)
                .where(
                        p.isActive.isTrue(),
                        excludedIds.isEmpty() ? null : p.id.notIn(excludedIds),
                        totalStock().gt(0))
                .orderBy(p.createdAt.desc())
                .limit(limit)
                .fetch();
    }

    private OrderSpecifier<?> getOrderSpecifier(String sortBy, String sortDirection, QProduct product) {
        boolean isAsc = "ASC".equalsIgnoreCase(sortDirection);

        if (sortBy == null || sortBy.isEmpty()) {
            return product.createdAt.desc();
        }

        return switch (sortBy.toLowerCase()) {
            case "price" -> isAsc ? product.price.asc() : product.price.desc();
            case "name" -> isAsc ? product.name.asc() : product.name.desc();
            case "rating" -> isAsc ? product.avgRating.asc() : product.avgRating.desc();
            case "sold" -> isAsc ? product.soldCount.asc() : product.soldCount.desc();
            default -> product.createdAt.desc();
        };
    }
}
