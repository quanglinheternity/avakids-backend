package com.example.avakids_backend.DTO.Wishlist;

import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WishlistCreateRequest {

    @NotNull(message = "PRODUCT_ID_NULL")
    private Long productId;
}
