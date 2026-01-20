package com.example.avakids_backend.mapper;

import org.mapstruct.*;

import com.example.avakids_backend.DTO.ProductOption.ProductOptionValueResponse;
import com.example.avakids_backend.entity.ProductOptionValue;

@Mapper(componentModel = "spring")
public interface ProductOptionValueMapper {

    ProductOptionValueResponse toResponseDTO(ProductOptionValue entity);
}
