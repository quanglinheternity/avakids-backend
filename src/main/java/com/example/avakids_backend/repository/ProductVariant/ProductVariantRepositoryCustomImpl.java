package com.example.avakids_backend.repository.ProductVariant;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.avakids_backend.DTO.ProductVariant.ProductAggregateResult;
import com.example.avakids_backend.DTO.ProductVariant.ProductOptionValueResponseDTO;
import com.example.avakids_backend.DTO.ProductVariant.ProductVariantDetailResponse;
import com.example.avakids_backend.DTO.ProductVariant.ProductVariantResponse;
import com.example.avakids_backend.entity.*;
import com.example.avakids_backend.mapper.ProductOptionValueMapper;
import com.example.avakids_backend.repository.ProductVariantImage.ProductVariantImageRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductVariantRepositoryCustomImpl implements ProductVariantRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final ProductVariantImageRepository productVariantImageRepository;
    private final ProductOptionValueMapper productOptionValueMapper;

    @Override
    public Optional<ProductVariantDetailResponse> findExactVariant(Long productId, List<Long> optionValueIds) {
        QProductVariant v = QProductVariant.productVariant;
        QProductOptionValue ov = QProductOptionValue.productOptionValue;
        QProduct p = QProduct.product;

        ProductVariantDetailResponse response;

        // ===== CASE 1: KHÔNG CÓ OPTION → LẤY DEFAULT VARIANT =====
        if (optionValueIds == null || optionValueIds.isEmpty()) {

            response = queryFactory
                    .select(Projections.bean(
                            ProductVariantDetailResponse.class,
                            v.id,
                            v.sku,
                            v.variantName,
                            v.price,
                            v.salePrice,
                            v.stockQuantity,
                            v.soldCount,
                            v.barcode,
                            v.isDefault,
                            p.avgRating,
                            p.reviewCount))
                    .from(v)
                    .leftJoin(v.product, p)
                    .where(v.product.id.eq(productId), v.isDefault.isTrue())
                    .fetchOne();

        }
        // ===== CASE 2: CÓ OPTION → MATCH CHÍNH XÁC =====
        else {

            long size = optionValueIds.size();

            response = queryFactory
                    .select(Projections.bean(
                            ProductVariantDetailResponse.class,
                            v.id,
                            v.sku,
                            v.variantName,
                            v.price,
                            v.salePrice,
                            v.stockQuantity,
                            v.soldCount,
                            v.barcode,
                            v.isDefault,
                            p.avgRating,
                            p.reviewCount))
                    .from(v)
                    .join(v.optionValues, ov)
                    .leftJoin(v.product, p)
                    .where(v.product.id.eq(productId), ov.id.in(optionValueIds))
                    .groupBy(v.id, p.avgRating, p.reviewCount)
                    .having(ov.id.count().eq(size), v.optionValues.size().eq((int) size))
                    .fetchOne();
        }

        // ===== KHÔNG TÌM THẤY VARIANT =====
        if (response == null) {
            return Optional.empty();
        }

        // ===== VARIANT CÓ OPTION → SET OPTION VALUES =====
        if (hasOptionByVariant(response.getId())) {
            response.setOptionValues(findOptionValuesByVariantId(response.getId()));
        }

        return Optional.of(response);
    }

    @Override
    public Optional<ProductVariantDetailResponse> findVariantBySku(Long productId, String sku) {

        QProductVariant v = QProductVariant.productVariant;
        QProduct p = QProduct.product;

        BooleanBuilder where = new BooleanBuilder();
        where.and(v.product.id.eq(productId));

        if (sku != null && !sku.isBlank()) {
            where.and(v.sku.eq(sku));
        } else {
            where.and(v.isDefault.isTrue());
        }

        // ===== QUERY VARIANT (KHÔNG JOIN OPTION) =====
        ProductVariantDetailResponse response = queryFactory
                .select(Projections.bean(
                        ProductVariantDetailResponse.class,
                        v.id,
                        v.sku,
                        v.variantName,
                        v.price,
                        v.salePrice,
                        v.stockQuantity,
                        v.soldCount,
                        v.barcode,
                        v.isDefault,
                        p.avgRating,
                        p.reviewCount))
                .from(v)
                .leftJoin(v.product, p)
                .where(where)
                .fetchOne();

        if (response == null) {
            return Optional.empty();
        }

        if (hasOptionByVariant(response.getId())) {
            response.setOptionValues(findOptionValuesByVariantId(response.getId()));
        }

        return Optional.of(response);
    }

    private boolean hasOptionByVariant(Long variantId) {

        QProductVariant v = QProductVariant.productVariant;
        QProductOptionValue ov = QProductOptionValue.productOptionValue;

        Integer exists = queryFactory
                .selectOne()
                .from(v)
                .join(v.optionValues, ov)
                .where(v.id.eq(variantId))
                .fetchFirst();

        return exists != null;
    }

    private List<ProductOptionValueResponseDTO> findOptionValuesByVariantId(Long variantId) {

        QProductVariant v = QProductVariant.productVariant;
        QProductOptionValue ov = QProductOptionValue.productOptionValue;
        QProductOption o = QProductOption.productOption;

        return queryFactory
                .select(Projections.bean(
                        ProductOptionValueResponseDTO.class, ov.id, o.name.as("optionName"), ov.value, ov.displayOrder))
                .from(v)
                .join(v.optionValues, ov)
                .join(ov.option, o)
                .where(v.id.eq(variantId))
                .orderBy(o.id.asc(), ov.displayOrder.asc())
                .fetch();
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

    @Override
    @Transactional
    public void resetDefaultVariant(Long productId) {
        QProductVariant v = QProductVariant.productVariant;

        queryFactory
                .update(v)
                .set(v.isDefault, false)
                .where(v.product.id.eq(productId))
                .execute();
    }

    @Override
    public List<ProductVariantResponse> getVariantsByProductId(Long productId) {

        List<ProductVariant> variants = findVariantsByProductId(productId);

        if (variants.isEmpty()) {
            return List.of();
        }

        List<Long> variantIds = variants.stream().map(ProductVariant::getId).toList();

        Map<Long, String> imageMap = productVariantImageRepository.loadPrimaryImages(variantIds);

        return variants.stream()
                .map(variant -> ProductVariantResponse.builder()
                        .id(variant.getId())
                        .sku(variant.getSku())
                        .variantName(variant.getVariantName())
                        .price(variant.getPrice())
                        .salePrice(variant.getSalePrice())
                        .stockQuantity(variant.getStockQuantity())
                        .soldCount(variant.getSoldCount())
                        .weight(variant.getWeight())
                        .dimensions(variant.getDimensions())
                        .barcode(variant.getBarcode())
                        .isDefault(variant.getIsDefault())
                        .optionValues(productOptionValueMapper.toListResponseDTO(variant.getOptionValues()))
                        .createdAt(variant.getCreatedAt())
                        .updatedAt(variant.getUpdatedAt())
                        .imageUrl(imageMap.get(variant.getId()))
                        .build())
                .toList();
    }

    private List<ProductVariant> findVariantsByProductId(Long productId) {
        return queryFactory
                .selectFrom(QProductVariant.productVariant)
                .where(QProductVariant.productVariant.product.id.eq(productId))
                .fetch();
    }
}
