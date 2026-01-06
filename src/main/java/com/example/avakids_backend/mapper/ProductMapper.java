package com.example.avakids_backend.mapper;

import org.mapstruct.*;

import com.example.avakids_backend.DTO.Product.ProductCreateRequest;
import com.example.avakids_backend.DTO.Product.ProductResponse;
import com.example.avakids_backend.DTO.Product.ProductUpdateRequest;
import com.example.avakids_backend.Entity.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "stockQuantity", defaultValue = "0")
    @Mapping(target = "isActive", defaultValue = "false")
    @Mapping(target = "isFeatured", defaultValue = "false")
    Product toEntity(ProductCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "category", ignore = true)
    void updateEntity(@MappingTarget Product product, ProductUpdateRequest request);

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    ProductResponse toResponse(Product product);
}
