package com.example.avakids_backend.mapper;

import org.mapstruct.*;

import com.example.avakids_backend.DTO.Voucher.VoucherCreateRequest;
import com.example.avakids_backend.DTO.Voucher.VoucherResponse;
import com.example.avakids_backend.DTO.Voucher.VoucherUpdateRequest;
import com.example.avakids_backend.entity.Voucher;

@Mapper(componentModel = "spring")
public interface VoucherMapper {
    @Mapping(target = "maxDiscountAmount", source = "maxDiscount")
    @Mapping(target = "remainingQuantity", expression = "java(voucher.getTotalQuantity() - voucher.getUsedQuantity())")
    @Mapping(
            target = "isAvailable",
            expression = "java(voucher.getIsActive() && (voucher.getTotalQuantity() - voucher.getUsedQuantity() > 0))")
    VoucherResponse toResponseDTO(Voucher voucher);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usedQuantity", constant = "0")
    @Mapping(target = "usageLimitPerUser", constant = "1")
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "maxDiscount", source = "maxDiscountAmount")
    Voucher toEntity(VoucherCreateRequest dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "maxDiscount", source = "maxDiscountAmount")
    void updateUserFromDTO(VoucherUpdateRequest dto, @MappingTarget Voucher voucher);
}
