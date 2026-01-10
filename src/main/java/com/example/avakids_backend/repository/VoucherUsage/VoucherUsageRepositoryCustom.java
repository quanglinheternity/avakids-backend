package com.example.avakids_backend.repository.VoucherUsage;

public interface VoucherUsageRepositoryCustom {
    Long countByVoucherIdAndUserId(Long voucherId, Long userId);
}
