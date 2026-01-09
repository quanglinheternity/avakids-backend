package com.example.avakids_backend.repository.VoucherUsage;

import com.example.avakids_backend.entity.QVoucherUsage;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VoucherUsageRepositoryImpl implements VoucherUsageRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    private final QVoucherUsage vu = QVoucherUsage.voucherUsage;
    @Override
    public Long countByVoucherIdAndUserId(Long voucherId, Long userId) {
        QVoucherUsage vu = QVoucherUsage.voucherUsage;

        return queryFactory
                .select(vu.count())
                .from(vu)
                .where(
                        vu.voucher.id.eq(voucherId),
                        vu.user.id.eq(userId)
                )
                .fetchOne();
    }
}
