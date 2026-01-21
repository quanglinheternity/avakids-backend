package com.example.avakids_backend.repository.ProductVariantImage;

import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.example.avakids_backend.entity.ProductVariantImage;
import com.example.avakids_backend.entity.QProductVariantImage;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductVariantImageRepositoryImpl implements ProductVariantImageRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

    private static final QProductVariantImage VARIANT_IMAGE = QProductVariantImage.productVariantImage;

    @Override
    public Optional<ProductVariantImage> findPrimaryImageByVariantId(Long variantId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(VARIANT_IMAGE)
                .where(VARIANT_IMAGE.variant.id.eq(variantId).and(VARIANT_IMAGE.isPrimary.isTrue()))
                .fetchOne());
    }

    @Override
    @Transactional
    public void resetPrimaryImagesByVariantId(Long variantId) {
        long updatedCount = queryFactory
                .update(VARIANT_IMAGE)
                .set(VARIANT_IMAGE.isPrimary, false)
                .where(VARIANT_IMAGE
                        .variant
                        .id
                        .eq(variantId)
                        .and(VARIANT_IMAGE.isPrimary.isTrue())) // Chỉ update nếu đang là true
                .execute();

        if (updatedCount > 0) {
            entityManager.flush();
            entityManager.clear();
        }
    }

    @Override
    @Transactional
    public void deleteByVariantId(Long variantId) {
        long deletedCount = queryFactory
                .delete(VARIANT_IMAGE)
                .where(VARIANT_IMAGE.variant.id.eq(variantId))
                .execute();

        if (deletedCount > 0) {
            entityManager.flush();
            entityManager.clear();
        }
    }

    @Override
    public Integer findMaxDisplayOrderByVariantId(Long variantId) {
        return Optional.ofNullable(queryFactory
                        .select(VARIANT_IMAGE.displayOrder.max())
                        .from(VARIANT_IMAGE)
                        .where(VARIANT_IMAGE.variant.id.eq(variantId))
                        .fetchOne())
                .orElse(0);
    }
}
