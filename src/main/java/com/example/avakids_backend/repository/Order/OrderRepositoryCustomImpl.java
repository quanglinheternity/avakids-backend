package com.example.avakids_backend.repository.Order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.example.avakids_backend.DTO.Order.OrderSearchRequest;
import com.example.avakids_backend.entity.*;
import com.example.avakids_backend.enums.OrderStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final QOrder order = QOrder.order;

    @Override
    public Page<Order> searchOrders(OrderSearchRequest request, Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();

        if (request.getStatus() != null) {
            builder.and(order.status.eq(request.getStatus()));
        }

        if (request.getOrderCode() != null && !request.getOrderCode().isBlank()) {
            builder.and(order.orderNumber.containsIgnoreCase(request.getOrderCode()));
        }

        if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
            String kw = "%" + request.getKeyword().trim().toLowerCase() + "%";
            builder.and(order.orderNumber
                    .lower()
                    .like(kw)
                    .or(order.user.fullName.lower().like(kw))
                    .or(order.user.email.lower().like(kw)));
        }

        if (request.getFromDate() != null) {
            builder.and(order.createdAt.goe(request.getFromDate().atStartOfDay()));
        }

        if (request.getToDate() != null) {
            builder.and(order.createdAt.loe(request.getToDate().atTime(23, 59, 59)));
        }

        JPAQuery<Order> query = queryFactory
                .selectFrom(order)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        if (pageable.getSort().isSorted()) {
            List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
            pageable.getSort().forEach(o -> {
                switch (o.getProperty()) {
                    case "id" -> orderSpecifiers.add(o.isAscending() ? order.id.asc() : order.id.desc());
                    case "status" -> orderSpecifiers.add(o.isAscending() ? order.status.asc() : order.status.desc());
                    case "createdAt" -> orderSpecifiers.add(
                            o.isAscending() ? order.createdAt.asc() : order.createdAt.desc());
                    default -> orderSpecifiers.add(order.createdAt.desc());
                }
            });
            query.orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]));
        } else {
            query.orderBy(order.createdAt.desc());
        }

        Long count =
                queryFactory.select(order.count()).from(order).where(builder).fetchOne();

        long total = count != null ? count : 0L;
        return new PageImpl<>(query.fetch(), pageable, total);
    }

    @Override
    public Set<Long> findPurchasedCategoryIds(Long customerId) {

        QOrder o = QOrder.order;
        QOrderItem oi = QOrderItem.orderItem;
        QProductVariant v = QProductVariant.productVariant;
        QProduct p = QProduct.product;

        return new HashSet<>(queryFactory
                .selectDistinct(p.category.id)
                .from(o)
                .join(o.orderItems, oi)
                .join(oi.variant, v)
                .join(v.product, p)
                .where(o.user.id.eq(customerId), o.status.eq(OrderStatus.COMPLETED))
                .fetch());
    }

    @Override
    public boolean hasPurchasedAnyVariantOfProduct(Long customerId, Long productId) {
        QOrder o = QOrder.order;
        QOrderItem oi = QOrderItem.orderItem;
        QProductVariant v = QProductVariant.productVariant;

        Integer result = queryFactory
                .selectOne()
                .from(o)
                .join(o.orderItems, oi)
                .join(oi.variant, v)
                .where(o.user.id.eq(customerId), o.status.eq(OrderStatus.COMPLETED), v.product.id.eq(productId))
                .fetchFirst(); // EXISTS-style

        return result != null;
    }

    @Override
    public int countPurchasesInCategory(Long customerId, Long categoryId) {
        QOrder o = QOrder.order;
        QOrderItem oi = QOrderItem.orderItem;
        QProductVariant v = QProductVariant.productVariant;
        QProduct p = QProduct.product;

        Long count = queryFactory
                .select(o.id.countDistinct())
                .from(o)
                .join(o.orderItems, oi)
                .join(oi.variant, v)
                .join(v.product, p)
                .where(o.user.id.eq(customerId), p.category.id.eq(categoryId), o.status.eq(OrderStatus.COMPLETED))
                .fetchOne();

        return count != null ? count.intValue() : 0;
    }

    @Override
    public BigDecimal getAverageOrderValue(Long customerId) {
        QOrder o = QOrder.order;

        Tuple result = queryFactory
                .select(o.totalAmount.sum(), o.id.count())
                .from(o)
                .where(o.user.id.eq(customerId), o.status.eq(OrderStatus.COMPLETED))
                .fetchOne();

        if (result == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal sum = result.get(o.totalAmount.sum());
        Long count = result.get(o.id.count());

        if (sum == null || count == null || count == 0) {
            return BigDecimal.ZERO;
        }

        return sum.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
    }
}
