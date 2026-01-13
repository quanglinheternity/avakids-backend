package com.example.avakids_backend.repository.Wishlist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.avakids_backend.entity.Wishlist;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long>, WishlistRepositoryCustom {

    boolean existsByUserIdAndProductId(Long userId, Long productId);

    void deleteByUserIdAndProductId(Long userId, Long productId);
}
