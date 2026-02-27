package com.example.avakids_backend.repository.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.avakids_backend.DTO.Product.ProductResponse;
import com.example.avakids_backend.DTO.Product.ProductSearchRequest;
import com.example.avakids_backend.entity.*;
import com.example.avakids_backend.repository.ProductImage.ProductImageRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final ProductImageRepository productImageRepository;
    private final JPAQueryFactory queryFactory;
    private final QProduct p = QProduct.product;
    private final QProductVariant v = QProductVariant.productVariant;
    private final QOrderItem oi = QOrderItem.orderItem;

    @Override
    public Page<ProductResponse> searchProducts(ProductSearchRequest criteria, Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();

        // Điều kiện tìm kiếm
        if (criteria.getKeyword() != null && !criteria.getKeyword().isEmpty()) {
            builder.and(p.name.containsIgnoreCase(criteria.getKeyword())
                    .or(p.description.containsIgnoreCase(criteria.getKeyword()))
                    .or(p.category.name.containsIgnoreCase(criteria.getKeyword())));
        }

        if (criteria.getCategoryId() != null) {
            builder.and(p.category.id.eq(criteria.getCategoryId()));
        }

        if (criteria.getMinPrice() != null) {
            NumberExpression<BigDecimal> effectiveMinPrice = new CaseBuilder()
                    .when(p.hasVariants.isTrue().and(p.minPrice.isNotNull()))
                    .then(p.minPrice)
                    .otherwise(p.price);
            builder.and(effectiveMinPrice.goe(criteria.getMinPrice()));
        }

        if (criteria.getMaxPrice() != null) {
            NumberExpression<BigDecimal> effectiveMaxPrice = new CaseBuilder()
                    .when(p.hasVariants.isTrue().and(p.maxPrice.isNotNull()))
                    .then(p.maxPrice)
                    .otherwise(p.price);
            builder.and(effectiveMaxPrice.loe(criteria.getMaxPrice()));
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

        List<ProductResponse> responses = buildProductResponses(products);
        // Đếm tổng số
        Long total = queryFactory.select(p.count()).from(p).where(builder).fetchOne();

        return new PageImpl<>(responses, pageable, total != null ? total : 0);
    }

    @Override
    public List<ProductResponse> findFeaturedProducts(int limit) {

        List<Product> products = queryFactory
                .selectFrom(p)
                .leftJoin(p.images)
                .fetchJoin()
                .where(p.isActive.eq(true).and(p.isFeatured.eq(true)).and(p.totalStock.gt(0)))
                .orderBy(p.soldCount.desc(), p.avgRating.desc())
                .limit(limit)
                .fetch();

        return buildProductResponses(products);
    }

    @Override
    public List<ProductResponse> findBestSellingProducts(int limit) {

        List<Product> products = queryFactory
                .selectFrom(p)
                .leftJoin(p.images)
                .fetchJoin()
                .where(p.isActive.eq(true).and(p.soldCount.gt(0)))
                .orderBy(p.soldCount.desc())
                .limit(limit)
                .fetch();

        return buildProductResponses(products);
    }

    @Override
    public List<ProductResponse> findNewProducts(int limit) {

        List<Product> products = queryFactory
                .selectFrom(p)
                .leftJoin(p.images)
                .fetchJoin()
                .where(p.isActive.eq(true).and(p.totalStock.gt(0)))
                .orderBy(p.createdAt.desc())
                .limit(limit)
                .fetch();
        return buildProductResponses(products);
    }

    @Override
    public List<ProductResponse> findRelatedProducts(Long productId, Long categoryId, int limit) {

        List<Product> products = queryFactory
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
        return buildProductResponses(products);
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
                .where(p.isActive.isTrue(), excludedIds.isEmpty() ? null : p.id.notIn(excludedIds))
                //                        totalStock().gt(0))
                .orderBy(p.createdAt.desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public Optional<Product> findByVariantId(Long variantId) {

        QProduct product = QProduct.product;
        QProductVariant variant = QProductVariant.productVariant;

        Product result = queryFactory
                .select(product)
                .from(variant)
                .join(variant.product, product)
                .where(variant.id.eq(variantId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    private OrderSpecifier<?> getOrderSpecifier(String sortBy, String sortDirection, QProduct product) {
        boolean isAsc = "ASC".equalsIgnoreCase(sortDirection);

        if (sortBy == null || sortBy.isEmpty()) {
            return product.createdAt.desc();
        }

        return switch (sortBy.toLowerCase()) {
            case "price" -> {
                NumberExpression<BigDecimal> effectivePrice = new CaseBuilder()
                        .when(product.hasVariants.isTrue())
                        .then(product.minPrice)
                        .otherwise(product.price);

                yield isAsc ? effectivePrice.asc() : effectivePrice.desc();
            }
            case "discount" -> {
                NumberExpression<BigDecimal> basePrice = new CaseBuilder()
                        .when(product.hasVariants.isTrue().and(product.minPrice.isNotNull()))
                        .then(product.minPrice)
                        .otherwise(product.price);

                NumberExpression<BigDecimal> discountExpr = new CaseBuilder()
                        .when(product.salePrice.isNotNull().and(basePrice.gt(BigDecimal.ZERO)))
                        .then(basePrice
                                .subtract(product.salePrice)
                                .divide(basePrice)
                                .multiply(BigDecimal.valueOf(100)))
                        .otherwise(BigDecimal.ZERO);

                yield isAsc ? discountExpr.asc() : discountExpr.desc();
            }
            case "createdAt" -> isAsc ? product.createdAt.asc() : product.createdAt.desc();
            case "name" -> isAsc ? product.name.asc() : product.name.desc();
            case "rating" -> isAsc ? product.avgRating.asc() : product.avgRating.desc();
            case "sold" -> isAsc ? product.soldCount.asc() : product.soldCount.desc();
            default -> product.createdAt.desc();
        };
    }

    public List<ProductResponse> mapToProductResponses(List<Product> products, Map<Long, String> imageMap) {
        return products.stream()
                .map(product -> ProductResponse.builder()
                        .id(product.getId())
                        .sku(product.getSku())
                        .name(product.getName())
                        .slug(product.getSlug())
                        .imageUlr(imageMap.get(product.getId()))
                        .categoryId(product.getCategory().getId())
                        .categoryName(product.getCategory().getName())
                        .description(product.getDescription())
                        .hasVariants(product.getHasVariants())
                        .price(product.getPrice())
                        .salePrice(product.getSalePrice())
                        .minPrice(product.getMinPrice())
                        .maxPrice(product.getMaxPrice())
                        .totalStock(product.getTotalStock())
                        .isActive(product.getIsActive())
                        .isFeatured(product.getIsFeatured())
                        .avgRating(product.getAvgRating())
                        .reviewCount(product.getReviewCount())
                        .soldCount(product.getSoldCount())
                        .createdAt(product.getCreatedAt())
                        .updatedAt(product.getUpdatedAt())
                        .build())
                .toList();
    }

    private List<ProductResponse> buildProductResponses(List<Product> products) {

        if (products == null || products.isEmpty()) {
            return List.of();
        }
        List<Long> productIds = products.stream().map(Product::getId).toList();

        Map<Long, String> imageMap = productImageRepository.loadPrimaryImages(productIds);

        return mapToProductResponses(products, imageMap);
    }
}
