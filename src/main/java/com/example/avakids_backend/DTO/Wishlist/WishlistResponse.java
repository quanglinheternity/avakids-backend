package com.example.avakids_backend.DTO.Wishlist;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WishlistResponse {
    private Long id;
    private UserDTO user;
    private ProductDTO product;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter
    @Setter
    public static class UserDTO {
        private Long id;
        private String username;
        private String email;
    }

    @Getter
    @Setter
    public static class ProductDTO {
        private Long id;
        private String name;
        private String imageUrl;
        private BigDecimal price;
        private Boolean isActive;
    }
}
