package com.example.avakids_backend.service.Voucher;

import com.example.avakids_backend.DTO.Voucher.*;
import com.example.avakids_backend.entity.Order;
import com.example.avakids_backend.entity.User;
import com.example.avakids_backend.entity.VoucherUsage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface VoucherService {
    VoucherResponse createVoucher(VoucherCreateRequest dto);
    VoucherResponse updateVoucher(Long id, VoucherUpdateRequest dto);
    VoucherResponse getVoucher(Long id);
    void deleteVoucher(Long id);
    Page<VoucherResponse> getAllVoucher(VoucherSearchRequest request, Pageable pageable);
    VoucherValidationResponse validateVoucher(VoucherValidationRequest dto);
    VoucherUsage applyVoucherToOrder(User user, String voucherCode, Order order, BigDecimal orderAmount);
}
