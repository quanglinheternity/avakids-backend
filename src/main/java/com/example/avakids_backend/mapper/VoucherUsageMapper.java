package com.example.avakids_backend.mapper;

import org.mapstruct.*;

import com.example.avakids_backend.DTO.VoucherUsage.VoucherUsageResponse;
import com.example.avakids_backend.entity.VoucherUsage;

@Mapper(componentModel = "spring")
public interface VoucherUsageMapper {

    VoucherUsageResponse toResponseDTO(VoucherUsage voucherUsage);
}
