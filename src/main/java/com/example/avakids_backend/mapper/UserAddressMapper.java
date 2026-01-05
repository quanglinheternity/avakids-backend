package com.example.avakids_backend.mapper;

import com.example.avakids_backend.DTO.UserAddress.UserAddressUpdateRequest;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.example.avakids_backend.DTO.UserAddress.UserAddressAddRequest;
import com.example.avakids_backend.DTO.UserAddress.UserAddressResponse;
import com.example.avakids_backend.Entity.UserAddress;

@Mapper(componentModel = "spring")
public interface UserAddressMapper {
    UserAddressResponse toResponseDTO(UserAddress userAddress);

    UserAddress toEntity(UserAddressAddRequest dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAddressFromDTO(UserAddressUpdateRequest dto, @MappingTarget UserAddress userAddress);
}
