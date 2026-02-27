package com.example.avakids_backend.repository.Wishlist;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.example.avakids_backend.DTO.Wishlist.WishlistSearchRequest;
import com.example.avakids_backend.entity.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class WishlistRepositoryCustomImpl implements WishlistRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final QWishlist wishlist = QWishlist.wishlist;
    private final QProduct product = QProduct.product;
    private final QUser user = QUser.user;

    @Override
    public Page<Wishlist> searchWishlists(WishlistSearchRequest request, Long userId, Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(wishlist.user.id.eq(userId));
        if (request.getIsActive() != null) {
            builder.and(product.isActive.eq(request.getIsActive()));
        }

        if (request.getCategoryId() != null) {
            builder.and(product.category.id.eq(request.getCategoryId()));
        }

        // Search by product name (keyword)
        if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
            builder.and(wishlist.product.name.containsIgnoreCase(request.getKeyword()));
        }

        JPAQuery<Wishlist> query = queryFactory
                .selectFrom(wishlist)
                .leftJoin(wishlist.product, product)
                .fetchJoin()
                .leftJoin(wishlist.user, user)
                .fetchJoin()
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        // Sorting
        if (pageable.getSort().isSorted()) {
            List<OrderSpecifier<?>> orders = new ArrayList<>();
            pageable.getSort().forEach(o -> {
                switch (o.getProperty()) {
                    case "createdAt" -> orders.add(
                            o.isAscending() ? wishlist.createdAt.asc() : wishlist.createdAt.desc());
                    case "id" -> orders.add(o.isAscending() ? wishlist.id.asc() : wishlist.id.desc());
                    default -> orders.add(wishlist.createdAt.desc());
                }
            });
            query.orderBy(orders.toArray(new OrderSpecifier[0]));
        } else {
            query.orderBy(wishlist.createdAt.desc());
        }

        // Count total
        Long total = queryFactory
                .select(wishlist.count())
                .from(wishlist)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(query.fetch(), pageable, total != null ? total : 0L);
    }

    @Override
    public List<Long> findProductIdsByUserId(Long userId) {

        QWishlist wishlist = QWishlist.wishlist;

        return queryFactory
                .select(wishlist.product.id)
                .from(wishlist)
                .where(wishlist.user.id.eq(userId))
                .fetch();
    }

    @Override
    public List<Product> findFavoriteProducts(Long userId, int limit) {

        QWishlist w = QWishlist.wishlist;
        QProduct p = QProduct.product;

        return queryFactory
                .select(p)
                .from(w)
                .join(w.product, p)
                .where(w.user.id.eq(userId))
                .orderBy(w.createdAt.desc()) // optional: lấy mới nhất trước
                .limit(limit)
                .fetch();
    }
}
