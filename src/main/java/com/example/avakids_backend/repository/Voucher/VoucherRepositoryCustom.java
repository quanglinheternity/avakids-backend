package com.example.avakids_backend.repository.Voucher;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.avakids_backend.DTO.Voucher.VoucherSearchRequest;
import com.example.avakids_backend.entity.Voucher;

public interface VoucherRepositoryCustom {
    Page<Voucher> searchVouchers(VoucherSearchRequest request, Pageable pageable);

    Optional<Voucher> findAvailableVoucherByCode(String code, LocalDateTime now);
}
