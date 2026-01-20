package com.example.avakids_backend.mapper;

import org.mapstruct.*;

import com.example.avakids_backend.DTO.ProductVariant.AddProductVariantRequest;
import com.example.avakids_backend.DTO.ProductVariant.ProductVariantResponse;
import com.example.avakids_backend.DTO.ProductVariant.UpdateProductVariantRequest;
import com.example.avakids_backend.entity.ProductVariant;

@Mapper(
        componentModel = "spring",
        uses = {ProductVariantImageMapper.class, ProductOptionValueMapper.class})
public interface ProductVariantMapper {

    /* ================= CREATE ================= */

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "optionValues", ignore = true)
    //    @Mapping(target = "images", source = "images")
    @Mapping(target = "soldCount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProductVariant toEntity(AddProductVariantRequest dto);

    /* ================= UPDATE ================= */

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "optionValues", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(UpdateProductVariantRequest dto, @MappingTarget ProductVariant entity);

    /* ================= RESPONSE ================= */

    ProductVariantResponse toResponse(ProductVariant entity);
}
