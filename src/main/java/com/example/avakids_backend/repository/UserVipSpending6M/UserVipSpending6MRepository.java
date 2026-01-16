package com.example.avakids_backend.repository.UserVipSpending6M;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.avakids_backend.entity.UserVipSpending6M;

@Repository
public interface UserVipSpending6MRepository extends JpaRepository<UserVipSpending6M, Long> {
    Optional<UserVipSpending6M> findByUserId(Long userId);
}
