package com.example.avakids_backend.repository.Voucher;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.example.avakids_backend.DTO.Voucher.VoucherSearchRequest;
import com.example.avakids_backend.entity.QVoucher;
import com.example.avakids_backend.entity.Voucher;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class VoucherRepositoryCustomImpl implements VoucherRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QVoucher voucher = QVoucher.voucher;

    @Override
    public Page<Voucher> searchVouchers(VoucherSearchRequest request, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
            String kw = "%" + request.getKeyword().trim().toLowerCase() + "%";
            builder.and(voucher.code.lower().like(kw).or(voucher.name.lower().like(kw)));
        }
        // Filter by code
        if (request.getCode() != null && !request.getCode().isBlank()) {
            builder.and(voucher.code.containsIgnoreCase(request.getCode()));
        }

        // Filter by name
        if (request.getName() != null && !request.getName().isBlank()) {
            builder.and(voucher.name.containsIgnoreCase(request.getName()));
        }

        // Filter by active status
        if (request.getIsActive() != null) {
            builder.and(voucher.isActive.eq(request.getIsActive()));
        }

        // Filter by date range (startAt / endAt)
        if (request.getFromDate() != null) {
            builder.and(voucher.startAt.goe(request.getFromDate().atStartOfDay()));
        }

        if (request.getToDate() != null) {
            builder.and(voucher.endAt.loe(request.getToDate().atTime(23, 59, 59)));
        }

        // Build query
        JPAQuery<Voucher> query = queryFactory
                .selectFrom(voucher)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        // Sorting
        if (pageable.getSort().isSorted()) {
            List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
            pageable.getSort().forEach(o -> {
                switch (o.getProperty()) {
                    case "id" -> orderSpecifiers.add(o.isAscending() ? voucher.id.asc() : voucher.id.desc());
                    case "startAt" -> orderSpecifiers.add(
                            o.isAscending() ? voucher.startAt.asc() : voucher.startAt.desc());
                    case "endAt" -> orderSpecifiers.add(o.isAscending() ? voucher.endAt.asc() : voucher.endAt.desc());
                    default -> orderSpecifiers.add(voucher.startAt.desc());
                }
            });
            query.orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]));
        } else {
            query.orderBy(voucher.startAt.desc());
        }

        // Count total
        Long count = queryFactory
                .select(voucher.count())
                .from(voucher)
                .where(builder)
                .fetchOne();
        long total = count != null ? count : 0L;

        return new PageImpl<>(query.fetch(), pageable, total);
    }

    @Override
    public Optional<Voucher> findAvailableVoucherByCode(String code, LocalDateTime now) {

        Voucher result = queryFactory
                .selectFrom(voucher)
                .where(
                        voucher.code.eq(code),
                        voucher.isActive.isTrue(),
                        voucher.startAt.loe(now),
                        voucher.endAt.goe(now),
                        voucher.totalQuantity.subtract(voucher.usedQuantity).gt(0))
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
