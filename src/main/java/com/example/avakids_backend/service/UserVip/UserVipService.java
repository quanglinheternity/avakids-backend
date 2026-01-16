package com.example.avakids_backend.service.UserVip;

import java.math.BigDecimal;

import com.example.avakids_backend.DTO.UserVip.RedeemPreviewResponse;

public interface UserVipService {
    void processOrderCompletion(Long userId, Long orderId, BigDecimal orderAmount);

    RedeemPreviewResponse previewRedeemPoints(BigDecimal orderAmount);

    BigDecimal redeemPoints(Long userId, BigDecimal orderAmount, String orderCode);

    void checkAndRenewVipTier(Long userId);

    void refundPoints(Long userId, BigDecimal totalAmount, String orderCode);
}
