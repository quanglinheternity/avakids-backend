package com.example.avakids_backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.example.avakids_backend.DTO.Wishlist.WishlistCreateRequest;
import com.example.avakids_backend.DTO.Wishlist.WishlistDTO;
import com.example.avakids_backend.DTO.Wishlist.WishlistResponse;
import com.example.avakids_backend.entity.Wishlist;

@Mapper(componentModel = "spring")
public interface WishlistMapper {

    WishlistMapper INSTANCE = Mappers.getMapper(WishlistMapper.class);

    // --- Chuyển Wishlist entity sang WishlistDTO ---
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productPrice", source = "product.price")
    WishlistDTO toDTO(Wishlist wishlist);

    // --- Chuyển Wishlist entity sang WishlistResponse (nested DTOs) ---
    @Mapping(target = "user.id", source = "user.id")
    @Mapping(target = "user.username", source = "user.fullName")
    @Mapping(target = "user.email", source = "user.email")
    @Mapping(target = "product.id", source = "product.id")
    @Mapping(target = "product.name", source = "product.name")
    @Mapping(target = "product.price", source = "product.price")
    @Mapping(target = "product.isActive", source = "product.isActive")
    WishlistResponse toResponse(Wishlist wishlist);

    Wishlist toEntity(WishlistCreateRequest request);
}
