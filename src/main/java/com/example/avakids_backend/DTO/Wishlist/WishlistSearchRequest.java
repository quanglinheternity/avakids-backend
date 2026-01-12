package com.example.avakids_backend.DTO.Wishlist;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WishlistSearchRequest {

    private String keyword;

    private Boolean isActive;

    private Long categoryId;
}
