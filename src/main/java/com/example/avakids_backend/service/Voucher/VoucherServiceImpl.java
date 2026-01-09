package com.example.avakids_backend.service.Voucher;

import com.example.avakids_backend.DTO.Order.OrderResponse;
import com.example.avakids_backend.DTO.Order.OrderSearchRequest;
import com.example.avakids_backend.DTO.Voucher.*;
import com.example.avakids_backend.DTO.VoucherUsage.VoucherUsageResponse;
import com.example.avakids_backend.entity.Order;
import com.example.avakids_backend.entity.User;
import com.example.avakids_backend.entity.VoucherUsage;
import com.example.avakids_backend.exception.AppException;
import com.example.avakids_backend.exception.ErrorCode;
import com.example.avakids_backend.repository.VoucherUsage.VoucherUsageRepository;
import com.example.avakids_backend.service.Authentication.auth.AuthenticationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.avakids_backend.entity.Voucher;
import com.example.avakids_backend.mapper.VoucherMapper;
import com.example.avakids_backend.repository.Voucher.VoucherRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoucherServiceImpl implements VoucherService {
    private final VoucherRepository voucherRepository;
    private final VoucherMapper voucherMapper;
    private final VoucherValidator voucherValidator;
    private final VoucherUsageRepository voucherUsageRepository;
    private final AuthenticationService authenticationService;


    @Transactional
    @Override
    public VoucherResponse createVoucher(VoucherCreateRequest dto) {
        // Validate
        voucherValidator.existsByCode(dto.getCode());

        voucherValidator.validateStartAtBeforeEndAt(dto.getEndAt(), dto.getStartAt());

        voucherValidator.validatePercentageDiscountValue(dto.getDiscountType(), dto.getDiscountValue());

        Voucher voucher = voucherMapper.toEntity(dto);

        voucher = voucherRepository.save(voucher);
        log.info("Created voucher: {}", voucher.getCode());

        return voucherMapper.toResponseDTO(voucher);
    }
    @Override
    @Transactional
    public VoucherResponse updateVoucher(Long id, VoucherUpdateRequest dto) {
        Voucher voucher = voucherValidator.getVoucherById(id);

        voucherValidator.validateStartAtBeforeEndAt(dto.getEndAt(), dto.getStartAt());
        voucherValidator.validateTotalQuantityBeforeUsedQuantity(dto.getTotalQuantity(),voucher.getUsedQuantity());
        voucherMapper.updateUserFromDTO(dto,voucher);
        voucher = voucherRepository.save( voucher);
        log.info("Updated voucher: {}", voucher.getCode());

        return voucherMapper.toResponseDTO(voucher);
    }
    @Override
    public VoucherResponse getVoucher(Long id) {
        Voucher voucher = voucherValidator.getVoucherById(id);
        return voucherMapper.toResponseDTO(voucher);
    }
    @Override
    @Transactional
    public void deleteVoucher(Long id) {
        Voucher voucher = voucherValidator.getVoucherById(id);
        voucherValidator.validateUsedQuantity(voucher.getUsedQuantity());
        voucherRepository.delete(voucher);
        log.info("Deleted voucher: {}", voucher.getCode());
    }
    @Transactional(readOnly = true)
    public Page<VoucherResponse> getAllVoucher(VoucherSearchRequest request, Pageable pageable) {
        return voucherRepository.searchVouchers(request, pageable).map(voucherMapper::toResponseDTO);
    }
    public VoucherValidationResponse validateVoucher( VoucherValidationRequest dto) {
        Long userId = authenticationService.getCurrentUser().getId();
        Voucher voucher = voucherRepository.findAvailableVoucherByCode(
                dto.getCode().toUpperCase(),
                LocalDateTime.now()
        ).orElse(null);

        if (voucher == null) {
            return VoucherValidationResponse.builder()
                    .isValid(false)
                    .message("Voucher không hợp lệ hoặc đã hết hạn")
                    .build();
        }
        if (voucher.getUsageLimitPerUser() != null) {
            Long usageCount = voucherUsageRepository.countByVoucherIdAndUserId(voucher.getId(), userId);
            if (usageCount >= voucher.getUsageLimitPerUser()) {
                return VoucherValidationResponse.builder()
                        .isValid(false)
                        .message("Bạn đã sử dụng hết số lần cho phép với voucher này")
                        .voucher(voucherMapper.toResponseDTO(voucher))
                        .build();
            }
        }

        if (dto.getOrderAmount().compareTo(voucher.getMinOrderAmount()) < 0) {
            return VoucherValidationResponse.builder()
                    .isValid(false)
                    .message("Đơn hàng chưa đạt giá trị tối thiểu: " + voucher.getMinOrderAmount())
                    .voucher(voucherMapper.toResponseDTO(voucher))
                    .build();
        }



        BigDecimal discountAmount = voucher.calculateDiscount(dto.getOrderAmount());
        BigDecimal finalAmount = dto.getOrderAmount().subtract(discountAmount);

        return VoucherValidationResponse.builder()
                .isValid(true)
                .message("Voucher hợp lệ")
                .discountAmount(discountAmount)
                .finalAmount(finalAmount)
                .voucher(voucherMapper.toResponseDTO(voucher))
                .build();
    }
    @Transactional
    public VoucherUsage applyVoucherToOrder(User user, String voucherCode, Order order, BigDecimal orderAmount) {
        Voucher voucher = voucherValidator.validateApplyVoucher(user.getId(),voucherCode,order,orderAmount);

        BigDecimal discountAmount = voucher.calculateDiscount(orderAmount);

        voucher.setUsedQuantity(voucher.getUsedQuantity() + 1);
        voucherRepository.save(voucher);

        VoucherUsage usage = VoucherUsage.builder()
                .voucher(voucher)
                .user(user)
                .order(order)
                .orderAmount(orderAmount)
                .discountAmount(discountAmount)
                .build();

        usage = voucherUsageRepository.save(usage);

        return usage;
    }

}
