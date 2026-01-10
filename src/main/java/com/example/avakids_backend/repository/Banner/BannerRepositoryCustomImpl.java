package com.example.avakids_backend.repository.Banner;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.example.avakids_backend.DTO.Banner.BannerSearchRequest;
import com.example.avakids_backend.entity.Banner;
import com.example.avakids_backend.entity.QBanner;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BannerRepositoryCustomImpl implements BannerRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final QBanner banner = QBanner.banner;

    @Override
    public Integer findMaxDisplayOrderByPosition(Banner.BannerPosition position) {

        return queryFactory
                .select(banner.displayOrder.max())
                .from(banner)
                .where(banner.position.eq(position))
                .fetchOne();
    }

    @Override
    public Page<Banner> searchBanners(BannerSearchRequest request, Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();

        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            String kw = "%" + request.getTitle().trim().toLowerCase() + "%";
            builder.and(banner.title.lower().like(kw));
        }

        if (request.getPosition() != null) {
            builder.and(banner.position.eq(request.getPosition()));
        }

        if (request.getIsActive() != null) {
            builder.and(banner.isActive.eq(request.getIsActive()));
        }

        if (request.getStartAt() != null) {
            builder.and(banner.startAt.goe(request.getStartAt()));
        }

        if (request.getEndAt() != null) {
            builder.and(banner.endAt.loe(request.getEndAt()));
        }

        JPAQuery<Banner> query = queryFactory
                .selectFrom(banner)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        // Apply sorting
        if (pageable.getSort().isSorted()) {
            pageable.getSort().forEach(order -> {
                if (order.getProperty().equals("createdAt")) {
                    query.orderBy(order.isAscending() ? banner.createdAt.asc() : banner.createdAt.desc());
                } else if (order.getProperty().equals("displayOrder")) {
                    query.orderBy(order.isAscending() ? banner.displayOrder.asc() : banner.displayOrder.desc());
                }
            });
        }

        List<Banner> content = query.fetch();

        Long total =
                queryFactory.select(banner.count()).from(banner).where(builder).fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }
}
