package com.example.avakids_backend.mapper;

import org.mapstruct.*;

import com.example.avakids_backend.DTO.ProductOption.OptionRequest;
import com.example.avakids_backend.DTO.ProductOption.ProductOptionResponse;
import com.example.avakids_backend.entity.Product;
import com.example.avakids_backend.entity.ProductOption;

@Mapper(
        componentModel = "spring",
        uses = {ProductOptionValueMapper.class})
public interface ProductOptionMapper {

    ProductOptionResponse toResponseDTO(ProductOption entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", source = "product")
    @Mapping(target = "values", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "name", source = "dto.name")
    ProductOption toEntity(OptionRequest dto, Product product);

    /* ========= After mapping (set values + FK) ========= */

    //    @AfterMapping
    //    default void mapValues(
    //            ProductOptionRequest dto,
    //            @MappingTarget ProductOption option
    //    ) {
    //        if (dto.getValues() == null) return;
    //
    //        dto.getValues().forEach(v ->
    //                option.addValue(
    //                        com.example.avakids_backend.entity.ProductOptionValue.builder()
    //                                .value(v.getValue())
    //                                .displayOrder(
    //                                        v.getDisplayOrder() != null ? v.getDisplayOrder() : 0
    //                                )
    //                                .build()
    //                )
    //        );
    //    }
}
