package com.example.avakids_backend.mapper.User;

import org.mapstruct.*;

import com.example.avakids_backend.DTO.User.UserCreateRequest;
import com.example.avakids_backend.DTO.User.UserResponse;
import com.example.avakids_backend.DTO.User.UserUpdateRequest;
import com.example.avakids_backend.Entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponseDTO(User user);

    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "emailVerifiedAt", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "cartItems", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    User toEntity(UserCreateRequest dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDTO(UserUpdateRequest dto, @MappingTarget User user);
}
