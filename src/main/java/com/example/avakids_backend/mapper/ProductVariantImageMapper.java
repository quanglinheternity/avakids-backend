package com.example.avakids_backend.mapper;

import org.mapstruct.*;

import com.example.avakids_backend.DTO.ProductVariant.ProductVariantImageRequest;
import com.example.avakids_backend.DTO.ProductVariant.ProductVariantImageResponse;
import com.example.avakids_backend.entity.ProductVariantImage;

@Mapper(componentModel = "spring")
public interface ProductVariantImageMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "variant", ignore = true)
    ProductVariantImage toEntity(ProductVariantImageRequest dto);

    ProductVariantImageResponse toResponse(ProductVariantImage entity);
}
