package com.example.avakids_backend.service.UserVip;

import org.springframework.stereotype.Component;

import com.example.avakids_backend.entity.UserPointRedemptionLog;
import com.example.avakids_backend.repository.UserVipSpending6M.UserPointRedemptionLogRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserPointRedemptionValidator {

    private final UserPointRedemptionLogRepository userPointRedemptionLogRepository;

    public UserPointRedemptionLog getRedeemLogOrThrow(String orderCode) {
        return userPointRedemptionLogRepository
                .findByReferenceIdAndAction(orderCode, "REDEEM")
                .orElse(null);
    }

    public boolean isRefunded(String orderCode) {
        return userPointRedemptionLogRepository.existsByReferenceIdAndAction(orderCode, "REFUND");
    }
}
