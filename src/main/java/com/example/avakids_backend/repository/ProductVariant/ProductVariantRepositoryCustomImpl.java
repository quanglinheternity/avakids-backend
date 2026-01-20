package com.example.avakids_backend.repository.ProductVariant;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.avakids_backend.DTO.ProductVariant.ProductAggregateResult;
import com.example.avakids_backend.entity.ProductVariant;
import com.example.avakids_backend.entity.QProductOptionValue;
import com.example.avakids_backend.entity.QProductVariant;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductVariantRepositoryCustomImpl implements ProductVariantRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<ProductVariant> findExactVariant(Long productId, List<Long> optionValueIds) {
        QProductVariant v = QProductVariant.productVariant;
        QProductOptionValue ov = QProductOptionValue.productOptionValue;
        if (optionValueIds == null || optionValueIds.isEmpty()) {
            ProductVariant defaultVariant = queryFactory
                    .selectFrom(v)
                    .where(v.product.id.eq(productId), v.isDefault.isTrue())
                    .fetchOne();

            return Optional.ofNullable(defaultVariant);
        }
        long size = optionValueIds.size();

        ProductVariant result = queryFactory
                .select(v)
                .from(v)
                .join(v.optionValues, ov)
                .where(v.product.id.eq(productId), ov.id.in(optionValueIds))
                .groupBy(v.id)
                .having(ov.id.count().eq(size), v.optionValues.size().eq((int) size))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<ProductVariant> findVariantBySku(Long productId, String sku) {
        QProductVariant v = QProductVariant.productVariant;

        if (sku != null && !sku.isBlank()) {
            ProductVariant variant = queryFactory
                    .selectFrom(v)
                    .where(v.product.id.eq(productId), v.sku.eq(sku))
                    .fetchOne();

            return Optional.ofNullable(variant);
        }

        ProductVariant defaultVariant = queryFactory
                .selectFrom(v)
                .where(v.product.id.eq(productId), v.isDefault.isTrue())
                .fetchOne();

        return Optional.ofNullable(defaultVariant);
    }

    @Override
    public boolean existsVariantWithExactOptions(Long productId, List<Long> optionValueIds) {
        QProductVariant v = QProductVariant.productVariant;
        QProductOptionValue ov = QProductOptionValue.productOptionValue;

        long size = optionValueIds.size();

        Long count = queryFactory
                .select(v.id.count())
                .from(v)
                .join(v.optionValues, ov)
                .where(v.product.id.eq(productId), ov.id.in(optionValueIds))
                .groupBy(v.id)
                .having(ov.id.countDistinct().eq(size), v.optionValues.size().eq((int) size))
                .fetchFirst();

        return count != null;
    }

    @Override
    public boolean existsOtherVariantWithExactOptions(
            Long productId, Long currentVariantId, List<Long> optionValueIds) {
        QProductVariant v = QProductVariant.productVariant;
        QProductOptionValue ov = QProductOptionValue.productOptionValue;

        long size = optionValueIds.size();

        Long count = queryFactory
                .select(v.id.count())
                .from(v)
                .join(v.optionValues, ov)
                .where(v.product.id.eq(productId), v.id.ne(currentVariantId), ov.id.in(optionValueIds))
                .groupBy(v.id)
                .having(ov.id.countDistinct().eq(size), v.optionValues.size().eq((int) size))
                .fetchFirst();

        return count != null;
    }

    @Override
    public ProductAggregateResult aggregateByProductId(Long productId) {
        QProductVariant v = QProductVariant.productVariant;

        return queryFactory
                .select(Projections.constructor(
                        ProductAggregateResult.class,
                        v.id.count(),
                        v.stockQuantity.sum().coalesce(0),
                        Expressions.cases()
                                .when(v.salePrice.isNotNull())
                                .then(v.salePrice)
                                .otherwise(v.price)
                                .min(),
                        Expressions.cases()
                                .when(v.salePrice.isNotNull())
                                .then(v.salePrice)
                                .otherwise(v.price)
                                .max()))
                .from(v)
                .where(v.product.id.eq(productId))
                .fetchOne();
    }

    @Override
    public Integer sumStockByProductId(Long productId) {
        QProductVariant v = QProductVariant.productVariant;

        Integer totalStock = queryFactory
                .select(v.stockQuantity.sum().coalesce(0))
                .from(v)
                .where(
                        v.product.id.eq(productId)
                        //                        v.product.isActive.isTrue()
                        )
                .fetchOne();

        return totalStock != null ? totalStock : 0;
    }
}
