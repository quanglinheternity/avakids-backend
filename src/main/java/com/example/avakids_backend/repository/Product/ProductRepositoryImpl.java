package com.example.avakids_backend.repository.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.avakids_backend.DTO.Product.ProductSearchRequest;
import com.example.avakids_backend.entity.Product;
import com.example.avakids_backend.entity.QProduct;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QProduct product = QProduct.product;

    @Override
    public Page<Product> searchProducts(ProductSearchRequest criteria, Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();

        // Điều kiện tìm kiếm
        if (criteria.getKeyword() != null && !criteria.getKeyword().isEmpty()) {
            builder.and(product.name
                    .containsIgnoreCase(criteria.getKeyword())
                    .or(product.description.containsIgnoreCase(criteria.getKeyword()))
                    .or(product.sku.containsIgnoreCase(criteria.getKeyword())));
        }

        if (criteria.getCategoryId() != null) {
            builder.and(product.category.id.eq(criteria.getCategoryId()));
        }

        if (criteria.getMinPrice() != null) {
            builder.and(product.price.goe(criteria.getMinPrice()));
        }

        if (criteria.getMaxPrice() != null) {
            builder.and(product.price.loe(criteria.getMaxPrice()));
        }

        if (criteria.getIsActive() != null) {
            builder.and(product.isActive.eq(criteria.getIsActive()));
        } else {
            // Mặc định chỉ lấy sản phẩm active cho user
            if (!criteria.isAdminSearch()) {
                builder.and(product.isActive.eq(true));
            }
        }

        if (criteria.getIsFeatured() != null) {
            builder.and(product.isFeatured.eq(criteria.getIsFeatured()));
        }

        if (criteria.getInStock() != null && criteria.getInStock()) {
            builder.and(product.totalStock.gt(0));
        }

        if (criteria.getMinRating() != null) {
            builder.and(product.avgRating.goe(criteria.getMinRating()));
        }

        // Sắp xếp
        OrderSpecifier<?> orderSpecifier =
                getOrderSpecifier(criteria.getSortBy(), criteria.getSortDirection(), product);

        // Truy vấn với phân trang
        List<Product> products = queryFactory
                .selectFrom(product)
                .leftJoin(product.category)
                .fetchJoin()
                .where(builder)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Đếm tổng số
        Long total = queryFactory
                .select(product.count())
                .from(product)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(products, pageable, total != null ? total : 0);
    }

    @Override
    public List<Product> findFeaturedProducts(int limit) {

        return queryFactory
                .selectFrom(product)
                .leftJoin(product.images)
                .fetchJoin()
                .where(product.isActive
                        .eq(true)
                        .and(product.isFeatured.eq(true))
                        .and(product.totalStock.gt(0)))
                .orderBy(product.soldCount.desc(), product.avgRating.desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public List<Product> findBestSellingProducts(int limit) {

        return queryFactory
                .selectFrom(product)
                .leftJoin(product.images)
                .fetchJoin()
                .where(product.isActive.eq(true).and(product.totalStock.gt(0)))
                .orderBy(product.soldCount.desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public List<Product> findNewProducts(int limit) {

        return queryFactory
                .selectFrom(product)
                .leftJoin(product.images)
                .fetchJoin()
                .where(product.isActive.eq(true).and(product.totalStock.gt(0)))
                .orderBy(product.createdAt.desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public List<Product> findRelatedProducts(Long productId, Long categoryId, int limit) {
        QProduct product = QProduct.product;

        return queryFactory
                .selectFrom(product)
                .leftJoin(product.images)
                .fetchJoin()
                .where(product.isActive
                        .eq(true)
                        .and(product.totalStock.gt(0))
                        .and(product.id.ne(productId))
                        .and(product.category.id.eq(categoryId)))
                .orderBy(product.soldCount.desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public Optional<Product> findByIdWithImages(Long id) {
        QProduct product = QProduct.product;

        Product result = queryFactory
                .selectFrom(product)
                .leftJoin(product.images)
                .fetchJoin()
                .leftJoin(product.category)
                .fetchJoin()
                .where(product.id.eq(id))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<Product> findBySlugWithDetails(String slug) {

        Product result = queryFactory
                .selectFrom(product)
                .leftJoin(product.images)
                .fetchJoin()
                .leftJoin(product.category)
                .fetchJoin()
                .where(product.slug.eq(slug).and(product.isActive.eq(true)))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    @Transactional
    public void updateProductRating(Long productId, BigDecimal avgRating, Integer reviewCount) {

        queryFactory
                .update(product)
                .set(product.avgRating, avgRating)
                .set(product.reviewCount, reviewCount)
                .set(product.updatedAt, LocalDateTime.now())
                .where(product.id.eq(productId))
                .execute();
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
            case "newest" -> product.createdAt.desc();
            default -> product.createdAt.desc();
        };
    }
}
