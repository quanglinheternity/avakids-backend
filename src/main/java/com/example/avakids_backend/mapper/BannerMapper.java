package com.example.avakids_backend.mapper;

import org.mapstruct.*;

import com.example.avakids_backend.DTO.Banner.BannerCreateRequest;
import com.example.avakids_backend.DTO.Banner.BannerResponse;
import com.example.avakids_backend.DTO.Banner.BannerUpdateRequest;
import com.example.avakids_backend.entity.Banner;

@Mapper(componentModel = "spring")
public interface BannerMapper {

    BannerResponse toDTO(Banner banner);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Banner toEntity(BannerCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(BannerUpdateRequest request, @MappingTarget Banner banner);
}
