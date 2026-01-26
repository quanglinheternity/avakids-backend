package com.example.avakids_backend.repository.Order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.example.avakids_backend.DTO.Dashboard.DashboardOverviewResponse;
import com.example.avakids_backend.DTO.Order.OrderSearchRequest;
import com.example.avakids_backend.entity.*;
import com.example.avakids_backend.enums.OrderStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.DateExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final QOrder order = QOrder.order;
    private final QProduct product = QProduct.product;
    private final QProductImage productImage = QProductImage.productImage;
    private final QOrderItem orderItem = QOrderItem.orderItem;
    private final QProductVariant variant = QProductVariant.productVariant;

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
    /**
     * Dashboard
     */
    @Override
    public Long countOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return queryFactory
                .select(order.count())
                .from(order)
                .where(order.createdAt.between(startDate, endDate))
                .fetchOne();
    }

    @Override
    public BigDecimal getTotalRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal revenue = queryFactory
                .select(order.totalAmount.sum())
                .from(order)
                .where(order.status.eq(OrderStatus.DELIVERED).and(order.createdAt.between(startDate, endDate)))
                .fetchOne();

        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    @Override
    public DashboardOverviewResponse.OrderCompletionRate getCompletionRate() {
        Long totalOrders = queryFactory.select(order.count()).from(order).fetchOne();

        Long completed = queryFactory
                .select(order.count())
                .from(order)
                .where(order.status.eq(OrderStatus.DELIVERED))
                .fetchOne();

        Long cancelled = queryFactory
                .select(order.count())
                .from(order)
                .where(order.status.eq(OrderStatus.CANCELLED))
                .fetchOne();

        if (totalOrders == null || totalOrders == 0) {
            return DashboardOverviewResponse.OrderCompletionRate.builder()
                    .completed(0L)
                    .cancelled(0L)
                    .completionRate(0.0)
                    .cancellationRate(0.0)
                    .build();
        }

        double completionRate = (completed * 100.0) / totalOrders;
        double cancellationRate = (cancelled * 100.0) / totalOrders;

        return DashboardOverviewResponse.OrderCompletionRate.builder()
                .completed(completed)
                .cancelled(cancelled)
                .completionRate(Math.round(completionRate * 100.0) / 100.0)
                .cancellationRate(Math.round(cancellationRate * 100.0) / 100.0)
                .build();
    }

    @Override
    public Long countPendingOrders() {
        return queryFactory
                .select(order.count())
                .from(order)
                .where(order.status.eq(OrderStatus.PENDING))
                .fetchOne();
    }

    @Override
    public List<DashboardOverviewResponse.RevenueByDate> getRevenueByDate(int days) {

        LocalDateTime startDate = LocalDate.now().minusDays(days - 1).atStartOfDay();

        DateExpression<java.sql.Date> dateExpr =
                Expressions.dateTemplate(java.sql.Date.class, "DATE({0})", order.createdAt);

        return queryFactory
                .select(Projections.constructor(
                        DashboardOverviewResponse.RevenueByDate.class, dateExpr, order.totalAmount.sum()))
                .from(order)
                .where(order.status.eq(OrderStatus.DELIVERED).and(order.createdAt.goe(startDate)))
                .groupBy(dateExpr)
                .orderBy(dateExpr.asc())
                .fetch();
    }

    @Override
    public List<DashboardOverviewResponse.OrderByStatus> getOrdersByStatus() {
        Long totalOrders = queryFactory.select(order.count()).from(order).fetchOne();

        if (totalOrders == null || totalOrders == 0) {
            return List.of();
        }

        List<DashboardOverviewResponse.OrderByStatus> results = queryFactory
                .select(Projections.constructor(
                        DashboardOverviewResponse.OrderByStatus.class,
                        order.status.stringValue(),
                        order.count(),
                        Expressions.constant(0.0)))
                .from(order)
                .groupBy(order.status)
                .fetch();

        // Calculate percentage
        results.forEach(result -> {
            double percentage = (result.getCount() * 100.0) / totalOrders;
            result.setPercentage(Math.round(percentage * 100.0) / 100.0);
        });

        return results;
    }

    @Override
    public List<DashboardOverviewResponse.TopProduct> getTopProducts(int limit) {
        return queryFactory
                .select(Projections.constructor(
                        DashboardOverviewResponse.TopProduct.class,
                        product.id,
                        product.name,
                        productImage.imageUrl,
                        orderItem.quantity.sum().longValue(),
                        orderItem.subtotal.sum()))
                .from(orderItem)
                .join(orderItem.order, order)
                .join(orderItem.variant, variant)
                .join(variant.product, product)
                .leftJoin(product.images, productImage)
                .on(productImage.displayOrder.eq(0))
                .where(order.status.eq(OrderStatus.DELIVERED))
                .groupBy(product.id, product.name, productImage.imageUrl)
                .orderBy(orderItem.quantity.sum().desc())
                .limit(limit)
                .fetch();
    }
}
