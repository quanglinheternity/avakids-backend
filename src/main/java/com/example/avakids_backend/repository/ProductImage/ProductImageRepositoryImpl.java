package com.example.avakids_backend.repository.ProductImage;

import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.example.avakids_backend.entity.ProductImage;
import com.example.avakids_backend.entity.QProductImage;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductImageRepositoryImpl implements ProductImageRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

    private static final QProductImage PRODUCT_IMAGE = QProductImage.productImage;

    @Override
    public Optional<ProductImage> findPrimaryImageByProductId(Long productId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(PRODUCT_IMAGE)
                .where(PRODUCT_IMAGE.product.id.eq(productId).and(PRODUCT_IMAGE.isPrimary.isTrue()))
                .fetchOne());
    }

    @Override
    @Transactional
    public void resetPrimaryImagesByProductId(Long productId) {
        long updatedCount = queryFactory
                .update(PRODUCT_IMAGE)
                .set(PRODUCT_IMAGE.isPrimary, false)
                .where(PRODUCT_IMAGE
                        .product
                        .id
                        .eq(productId)
                        .and(PRODUCT_IMAGE.isPrimary.isTrue())) // Chỉ update nếu đang là true
                .execute();

        if (updatedCount > 0) {
            entityManager.flush();
            entityManager.clear();
        }
    }

    @Override
    @Transactional
    public void deleteByProductId(Long productId) {
        long deletedCount = queryFactory
                .delete(PRODUCT_IMAGE)
                .where(PRODUCT_IMAGE.product.id.eq(productId))
                .execute();

        if (deletedCount > 0) {
            entityManager.flush();
            entityManager.clear();
        }
    }

    @Override
    public Integer findMaxDisplayOrderByProductId(Long productId) {
        return Optional.ofNullable(queryFactory
                        .select(PRODUCT_IMAGE.displayOrder.max())
                        .from(PRODUCT_IMAGE)
                        .where(PRODUCT_IMAGE.product.id.eq(productId))
                        .fetchOne())
                .orElse(0);
    }
}
