package com.example.avakids_backend.mapper;

import java.math.BigDecimal;

import org.mapstruct.*;

import com.example.avakids_backend.DTO.CartItem.CartItemResponse;
import com.example.avakids_backend.entity.CartItem;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CartItemMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    //    @Mapping(target = "productImage", source = "product.image")
    @Mapping(target = "productPrice", source = "product.price")
    @Mapping(target = "productStock", source = "product.stockQuantity")
    @Mapping(target = "subtotal", expression = "java(calculateSubtotal(cartItem))")
    CartItemResponse toDTO(CartItem cartItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CartItem toEntity(CartItemResponse dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(CartItemResponse dto, @MappingTarget CartItem entity);

    default BigDecimal calculateSubtotal(CartItem cartItem) {
        if (cartItem.getProduct() == null
                || cartItem.getProduct().getPrice() == null
                || cartItem.getQuantity() == null) {
            return BigDecimal.ZERO;
        }

        return cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
    }
}
