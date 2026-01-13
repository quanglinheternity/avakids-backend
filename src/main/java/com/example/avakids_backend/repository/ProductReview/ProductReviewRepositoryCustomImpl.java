package com.example.avakids_backend.repository.ProductReview;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.example.avakids_backend.DTO.ProductReview.ProductReviewSearchRequest;
import com.example.avakids_backend.DTO.ProductReview.ProductReviewStats;
import com.example.avakids_backend.entity.ProductReview;
import com.example.avakids_backend.entity.QProductReview;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductReviewRepositoryCustomImpl implements ProductReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QProductReview qProductReview = QProductReview.productReview;

    @Override
    public Page<ProductReview> searchReviews(ProductReviewSearchRequest searchRequest, Pageable pageable) {
        BooleanBuilder predicate = new BooleanBuilder();

        if (searchRequest.getProductId() != null) {
            predicate.and(qProductReview.product.id.eq(searchRequest.getProductId()));
        }

        if (searchRequest.getUserId() != null) {
            predicate.and(qProductReview.user.id.eq(searchRequest.getUserId()));
        }

        if (searchRequest.getOrderId() != null) {
            predicate.and(qProductReview.order.id.eq(searchRequest.getOrderId()));
        }

        if (searchRequest.getRating() != null) {
            predicate.and(qProductReview.rating.eq(searchRequest.getRating()));
        }

        if (searchRequest.getMinRating() != null) {
            predicate.and(qProductReview.rating.goe(searchRequest.getMinRating()));
        }

        if (searchRequest.getMaxRating() != null) {
            predicate.and(qProductReview.rating.loe(searchRequest.getMaxRating()));
        }

        if (searchRequest.getIsVerifiedPurchase() != null) {
            predicate.and(qProductReview.isVerifiedPurchase.eq(searchRequest.getIsVerifiedPurchase()));
        }

        if (searchRequest.getHasImages() != null && searchRequest.getHasImages()) {
            predicate.and(qProductReview.imageUrl.isNotNull().and(qProductReview.imageUrl.ne("")));
        }

        if (searchRequest.getKeyword() != null && !searchRequest.getKeyword().isEmpty()) {
            predicate.and(qProductReview
                    .content
                    .containsIgnoreCase(searchRequest.getKeyword())
                    .or(qProductReview.user.fullName.containsIgnoreCase(searchRequest.getKeyword())));
        }

        if (searchRequest.getStartDate() != null) {
            predicate.and(qProductReview.createdAt.goe(searchRequest.getStartDate()));
        }

        if (searchRequest.getEndDate() != null) {
            predicate.and(qProductReview.createdAt.loe(searchRequest.getEndDate()));
        }

        JPAQuery<ProductReview> query =
                queryFactory.selectFrom(qProductReview).where(predicate).orderBy(qProductReview.createdAt.desc());

        Long total = queryFactory
                .select(qProductReview.id.count())
                .from(qProductReview)
                .where(predicate)
                .fetchOne();
        List<ProductReview> content =
                query.offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    @Override
    public ProductReviewStats getReviewStats(Long productId) {
        Tuple basicStats = queryFactory
                .select(qProductReview.rating.avg().coalesce(0.0), qProductReview.count())
                .from(qProductReview)
                .where(qProductReview.product.id.eq(productId))
                .fetchOne();

        Long verifiedCount = queryFactory
                .select(qProductReview.count())
                .from(qProductReview)
                .where(qProductReview.product.id.eq(productId).and(qProductReview.isVerifiedPurchase.isTrue()))
                .fetchOne();

        Long hasImagesCount = queryFactory
                .select(qProductReview.count())
                .from(qProductReview)
                .where(qProductReview
                        .product
                        .id
                        .eq(productId)
                        .and(qProductReview.imageUrl.isNotNull())
                        .and(qProductReview.imageUrl.ne("")))
                .fetchOne();

        List<Tuple> ratingDistribution = queryFactory
                .select(qProductReview.rating, qProductReview.count())
                .from(qProductReview)
                .where(qProductReview.product.id.eq(productId))
                .groupBy(qProductReview.rating)
                .orderBy(qProductReview.rating.asc())
                .fetch();

        Map<Integer, Long> distributionMap = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            distributionMap.put(i, 0L);
        }

        for (Tuple tuple : ratingDistribution) {
            Integer rating = tuple.get(qProductReview.rating);
            Long count = tuple.get(qProductReview.count());
            distributionMap.put(rating, count);
        }

        return ProductReviewStats.builder()
                .averageRating(
                        BigDecimal.valueOf(basicStats.get(0, Double.class)).setScale(2, RoundingMode.HALF_UP))
                .totalReviews(basicStats.get(1, Long.class))
                .verifiedPurchaseCount(verifiedCount != null ? verifiedCount : 0L)
                .hasImagesCount(hasImagesCount != null ? hasImagesCount : 0L)
                .ratingDistribution(distributionMap)
                .build();
    }

    @Override
    public Page<ProductReview> findByProductIdPage(Long productId, Pageable pageable) {
        QProductReview review = QProductReview.productReview;

        // Query lấy data
        List<ProductReview> reviews = queryFactory
                .selectFrom(review)
                .where(review.product.id.eq(productId))
                .orderBy(review.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Query đếm tổng
        Long total = queryFactory
                .select(review.count())
                .from(review)
                .where(review.product.id.eq(productId))
                .fetchOne();

        return new PageImpl<>(reviews, pageable, total == null ? 0 : total);
    }
}
