package com.example.avakids_backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.avakids_backend.DTO.ProductImage.ProductImageResponse;
import com.example.avakids_backend.entity.ProductVariantImage;

@Mapper(componentModel = "spring")
public interface ProductVariantImageMapper {

    @Mapping(source = "variant.id", target = "productId")
    ProductImageResponse toResponse(ProductVariantImage variantImage);
}
