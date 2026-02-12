package com.example.avakids_backend.mapper;

import org.mapstruct.*;
import org.mapstruct.ReportingPolicy;

import com.example.avakids_backend.DTO.Blog.BlogCreateRequest;
import com.example.avakids_backend.DTO.Blog.BlogDtoResponse;
import com.example.avakids_backend.DTO.Blog.BlogResponse;
import com.example.avakids_backend.DTO.Blog.BlogUpdateRequest;
import com.example.avakids_backend.entity.Blog;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BlogMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "viewCount", constant = "0")
    @Mapping(target = "publishedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Blog toEntity(BlogCreateRequest dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true) // slug không cho update
    @Mapping(target = "viewCount", ignore = true)
    @Mapping(target = "publishedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(BlogUpdateRequest dto, @MappingTarget Blog blog);

    BlogResponse toResponse(Blog blog);

    BlogDtoResponse toDtoResponse(Blog blog);
}
