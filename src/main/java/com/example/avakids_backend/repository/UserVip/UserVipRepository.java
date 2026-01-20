package com.example.avakids_backend.repository.UserVip;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.avakids_backend.entity.UserVip;

@Repository
public interface UserVipRepository extends JpaRepository<UserVip, Long> {
    Optional<UserVip> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    List<UserVip> findByTierExpiresAtLessThanEqual(LocalDateTime time);
}
