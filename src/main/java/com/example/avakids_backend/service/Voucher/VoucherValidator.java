package com.example.avakids_backend.service.Voucher;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.example.avakids_backend.entity.Order;
import com.example.avakids_backend.entity.Voucher;
import com.example.avakids_backend.exception.AppException;
import com.example.avakids_backend.exception.ErrorCode;
import com.example.avakids_backend.repository.Voucher.VoucherRepository;
import com.example.avakids_backend.repository.VoucherUsage.VoucherUsageRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class VoucherValidator {
    private final VoucherRepository voucherRepository;
    private final VoucherUsageRepository voucherUsageRepository;

    public void existsByCode(String code) {
        if (voucherRepository.existsByCode(code)) {
            throw new AppException(ErrorCode.VOUCHER_CODE_ALREADY_EXISTS);
        }
    }

    public void validateStartAtBeforeEndAt(LocalDateTime endAt, LocalDateTime startAt) {
        if (endAt.isBefore(startAt)) {
            throw new AppException(ErrorCode.END_TIME_BEFORE_START_TIME);
        }
    }

    public void validatePercentageDiscountValue(Voucher.DiscountType discountType, BigDecimal discountValue) {
        if (discountType == Voucher.DiscountType.PERCENTAGE && discountValue.compareTo(BigDecimal.valueOf(100)) > 0) {

            throw new AppException(ErrorCode.DISCOUNT_PERCENTAGE_INVALID);
        }
    }

    public void validateUsedQuantity(Integer usedQuantity) {
        if (usedQuantity > 0) {
            throw new AppException(ErrorCode.VOUCHER_INVALID);
        }
    }

    public Voucher getVoucherById(Long id) {
        return voucherRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));
    }

    public void validateTotalQuantityBeforeUsedQuantity(Integer totalQuantity, Integer usedQuantity) {
        if (totalQuantity != null) {
            if (totalQuantity < usedQuantity) {
                throw new AppException(ErrorCode.VOUCHER_QUANTITY_INVALID);
            }
        }
    }

    public Voucher validateApplyVoucher(Long userId, String voucherCode, Order order, BigDecimal orderAmount) {

        Voucher voucher = getValidVoucher(voucherCode);

        validateOrderNotUsedVoucher(order);
        validateMinOrderAmount(voucher, orderAmount);
        validateUsageLimitPerUser(voucher, userId);

        return voucher;
    }

    private Voucher getValidVoucher(String voucherCode) {
        return voucherRepository
                .findAvailableVoucherByCode(voucherCode.toUpperCase(), LocalDateTime.now())
                .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_INVALID));
    }

    private void validateOrderNotUsedVoucher(Order order) {
        if (voucherUsageRepository.existsByOrderId(order.getId())) {
            throw new AppException(ErrorCode.ORDER_ALREADY_USED_VOUCHER);
        }
    }

    private void validateMinOrderAmount(Voucher voucher, BigDecimal orderAmount) {
        if (orderAmount.compareTo(voucher.getMinOrderAmount()) < 0) {
            throw new AppException(ErrorCode.ORDER_AMOUNT_TOO_LOW);
        }
    }

    private void validateUsageLimitPerUser(Voucher voucher, Long userId) {
        if (voucher.getUsageLimitPerUser() == null) {
            return;
        }

        Long usageCount = voucherUsageRepository.countByVoucherIdAndUserId(voucher.getId(), userId);

        if (usageCount >= voucher.getUsageLimitPerUser()) {
            throw new AppException(ErrorCode.VOUCHER_USAGE_LIMIT_EXCEEDED);
        }
    }
}
