package com.example.avakids_backend.repository.ProductOption;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.avakids_backend.entity.ProductOption;
import com.example.avakids_backend.entity.QProductOption;
import com.example.avakids_backend.entity.QProductOptionValue;
import com.example.avakids_backend.entity.QProductVariant;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductOptionRepositoryCustomImpl implements ProductOptionRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<ProductOption> findOptionsHasVariantByProductId(Long productId) {
        QProductOption o = QProductOption.productOption;
        QProductOptionValue ov = QProductOptionValue.productOptionValue;
        QProductVariant v = QProductVariant.productVariant;

        return queryFactory
                .selectDistinct(o)
                .from(o)
                .where(
                        o.product.id.eq(productId),
                        JPAExpressions.selectOne()
                                .from(v)
                                .join(v.optionValues, ov)
                                .where(ov.option.eq(o))
                                .exists())
                .fetch();
    }
}
