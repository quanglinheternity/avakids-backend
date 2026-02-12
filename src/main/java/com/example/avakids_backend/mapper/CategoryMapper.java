package com.example.avakids_backend.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.example.avakids_backend.DTO.Category.CategoryCreateRequest;
import com.example.avakids_backend.DTO.Category.CategoryResponse;
import com.example.avakids_backend.DTO.Category.CategoryUpdateRequest;
import com.example.avakids_backend.entity.Category;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(target = "children", ignore = true)
    CategoryResponse toResponse(Category category);
    default CategoryResponse toResponseWithChildren(
            Category parent,
            List<Category> children
    ) {
        CategoryResponse response = toResponse(parent);

        response.setChildren(
                children.stream()
                        .map(this::toResponse)
                        .toList()
        );

        return response;
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "parent", ignore = true)
    Category toEntity(CategoryCreateRequest dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "parent", ignore = true)
    void updateEntityFromDTO(CategoryUpdateRequest dto, @MappingTarget Category category);
}
