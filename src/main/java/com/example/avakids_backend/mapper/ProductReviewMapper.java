package com.example.avakids_backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.avakids_backend.DTO.ProductReview.ProductReviewCreateRequest;
import com.example.avakids_backend.DTO.ProductReview.ProductReviewResponse;
import com.example.avakids_backend.entity.ProductReview;

@Mapper(componentModel = "spring")
public interface ProductReviewMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "isVerifiedPurchase", ignore = true)
    ProductReview toEntity(ProductReviewCreateRequest request);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "user.fullName", target = "userName")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.avatarUrl", target = "userAvatar")
    @Mapping(source = "order.orderNumber", target = "orderCode")
    ProductReviewResponse toResponse(ProductReview review);
}
