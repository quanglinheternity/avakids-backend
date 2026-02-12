package com.example.avakids_backend.repository.CartItem;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.example.avakids_backend.DTO.CartItem.CartItemResponse;
import com.example.avakids_backend.entity.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CartItemRepositoryCustomImpl implements CartItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QCartItem cartItem = QCartItem.cartItem;
    private final QProduct product = QProduct.product;
    private final QProductVariant variant = QProductVariant.productVariant;

    @Override
    public Page<CartItem> searchCartItems(
            Long userId, String keyWord, Long productId, Integer minQuantity, Integer maxQuantity, Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();
        if (keyWord != null && !keyWord.trim().isEmpty()) {
            String kw = "%" + keyWord.trim().toLowerCase() + "%";
            builder.and(cartItem.variant.variantName.lower().like(kw));
        }
        if (userId != null) {
            builder.and(cartItem.user.id.eq(userId));
        }

        if (productId != null) {
            builder.and(cartItem.variant.id.eq(productId));
        }

        if (minQuantity != null) {
            builder.and(cartItem.quantity.goe(minQuantity));
        }

        if (maxQuantity != null) {
            builder.and(cartItem.quantity.loe(maxQuantity));
        }

        JPAQuery<CartItem> query = queryFactory
                .selectFrom(cartItem)
                .leftJoin(cartItem.variant, variant)
                .fetchJoin()
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        // Apply sorting
        if (pageable.getSort().isSorted()) {
            pageable.getSort().forEach(order -> {
                if (order.getProperty().equals("createdAt")) {
                    query.orderBy(order.isAscending() ? cartItem.createdAt.asc() : cartItem.createdAt.desc());
                } else if (order.getProperty().equals("quantity")) {
                    query.orderBy(order.isAscending() ? cartItem.quantity.asc() : cartItem.quantity.desc());
                }
            });
        }

        List<CartItem> content = query.fetch();

        Long total = queryFactory
                .select(cartItem.count())
                .from(cartItem)
                .join(cartItem.variant, variant)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    @Override
    public List<CartItemResponse> findCartItemResponses(Long userId) {
        QCartItem cartItem = QCartItem.cartItem;
        QProductVariant variant = QProductVariant.productVariant;
        QProduct product = QProduct.product;
        QProductImage productImage = QProductImage.productImage;
        return queryFactory
                .select(Projections.constructor(
                        CartItemResponse.class,
                        cartItem.id,
                        variant.id,
                        variant.variantName,
                        productImage.imageUrl,
                        variant.price,
                        variant.stockQuantity,
                        cartItem.quantity,
                        variant.price.multiply(cartItem.quantity.castToNum(BigDecimal.class)),
                        cartItem.createdAt,
                        cartItem.updatedAt))
                .from(cartItem)
                .join(cartItem.variant, variant)
                .join(variant.product, product)
                .leftJoin(product.images, productImage)
                .on(productImage.isPrimary.isTrue())
                .where(cartItem.user.id.eq(userId))
                .fetch();
    }

    @Override
    public void deleteOutOfStockItems(Long userId) {

        queryFactory
                .delete(cartItem)
                .where(cartItem.user.id.eq(userId).and(cartItem.variant.stockQuantity.loe(0)))
                .execute();
    }
}
