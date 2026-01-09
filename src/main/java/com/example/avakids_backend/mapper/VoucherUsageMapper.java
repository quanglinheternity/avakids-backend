package com.example.avakids_backend.mapper;

import com.example.avakids_backend.DTO.VoucherUsage.VoucherUsageResponse;
import com.example.avakids_backend.entity.VoucherUsage;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface VoucherUsageMapper {

    VoucherUsageResponse toResponseDTO(VoucherUsage voucherUsage);


}
