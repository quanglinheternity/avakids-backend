package com.example.avakids_backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.avakids_backend.DTO.ProductImage.ProductImageResponse;
import com.example.avakids_backend.entity.ProductImage;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {

    @Mapping(source = "product.id", target = "productId")
    ProductImageResponse toResponse(ProductImage productImage);
}
