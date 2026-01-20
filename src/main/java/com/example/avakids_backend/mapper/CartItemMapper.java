package com.example.avakids_backend.mapper;

import java.math.BigDecimal;

import org.mapstruct.*;

import com.example.avakids_backend.DTO.CartItem.CartItemResponse;
import com.example.avakids_backend.entity.CartItem;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CartItemMapper {

    @Mapping(target = "variantId", source = "variant.id")
    @Mapping(target = "variantName", source = "variant.variantName")
    //    @Mapping(target = "variant Image", source = "variant .image")
    @Mapping(target = "variantPrice", source = "variant.price")
    @Mapping(target = "variantStock", source = "variant.stockQuantity")
    @Mapping(target = "subtotal", expression = "java(calculateSubtotal(cartItem))")
    CartItemResponse toDTO(CartItem cartItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "variant ", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CartItem toEntity(CartItemResponse dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "variant ", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(CartItemResponse dto, @MappingTarget CartItem entity);

    default BigDecimal calculateSubtotal(CartItem cartItem) {
        if (cartItem.getVariant() == null
                || cartItem.getVariant().getPrice() == null
                || cartItem.getQuantity() == null) {
            return BigDecimal.ZERO;
        }

        return cartItem.getVariant().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
    }
}
