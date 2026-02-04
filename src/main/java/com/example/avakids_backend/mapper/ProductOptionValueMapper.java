package com.example.avakids_backend.mapper;

import java.util.List;

import org.mapstruct.*;

import com.example.avakids_backend.DTO.ProductOption.ProductOptionValueResponse;
import com.example.avakids_backend.DTO.ProductVariant.ProductOptionValueResponseDTO;
import com.example.avakids_backend.entity.ProductOptionValue;

@Mapper(componentModel = "spring")
public interface ProductOptionValueMapper {

    ProductOptionValueResponse toResponseDTO(ProductOptionValue entity);

    List<ProductOptionValueResponseDTO> toListResponseDTO(List<ProductOptionValue> entities);
}
