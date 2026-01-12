package com.example.avakids_backend.repository.Wishlist;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.avakids_backend.entity.Wishlist;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long>, WishlistRepositoryCustom {

    List<Wishlist> findByUserId(Long userId);

    Page<Wishlist> findByUserId(Long userId, Pageable pageable);

    Optional<Wishlist> findByUserIdAndProductId(Long userId, Long productId);

    boolean existsByUserIdAndProductId(Long userId, Long productId);

    void deleteByUserIdAndProductId(Long userId, Long productId);

    int countByUserId(Long userId);

    @Query("SELECT w FROM Wishlist w JOIN FETCH w.product p WHERE w.user.id = :userId AND p.isActive = true")
    List<Wishlist> findActiveProductsByUserId(@Param("userId") Long userId);

    @Query("SELECT w FROM Wishlist w WHERE w.user.id = :userId AND w.product.id IN :productIds")
    List<Wishlist> findByUserIdAndProductIds(@Param("userId") Long userId, @Param("productIds") List<Long> productIds);
}
