package com.example.avakids_backend.repository.Order;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.example.avakids_backend.DTO.Order.OrderSearchRequest;
import com.example.avakids_backend.entity.Order;
import com.example.avakids_backend.entity.QOrder;
import com.querydsl.core.BooleanBuilder;
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
}
