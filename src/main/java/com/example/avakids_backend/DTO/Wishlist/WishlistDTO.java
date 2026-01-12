package com.example.avakids_backend.DTO.Wishlist;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WishlistDTO {
    private Long id;
    private Long userId;
    private Long productId;
    private String productName;
    private String productImage;
    private Double productPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
