package com.example.avakids_backend.service.UserVip;

import org.springframework.stereotype.Component;

import com.example.avakids_backend.entity.UserPointRedemptionLog;
import com.example.avakids_backend.exception.AppException;
import com.example.avakids_backend.exception.ErrorCode;
import com.example.avakids_backend.repository.UserVipSpending6M.UserPointRedemptionLogRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserPointRedemptionValidator {

    private final UserPointRedemptionLogRepository userPointRedemptionLogRepository;

    public UserPointRedemptionLog getRedeemLogOrThrow(String orderCode) {
        return userPointRedemptionLogRepository
                .findByReferenceIdAndAction(orderCode, "REDEEM")
                .orElseThrow(() -> new AppException(ErrorCode.POINT_REDEEM_LOG_NOT_FOUND));
    }

    public boolean isRefunded(String orderCode) {
        return userPointRedemptionLogRepository
                .existsByReferenceIdAndAction(orderCode, "REFUND");
    }
}
