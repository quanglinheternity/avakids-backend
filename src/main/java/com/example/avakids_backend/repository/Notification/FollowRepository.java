package com.example.avakids_backend.repository.Notification;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.avakids_backend.entity.Follow;

public interface FollowRepository extends JpaRepository<Follow, Long>, FollowRepositoryCustom {

    List<Follow> findByUserId(Long userId);
}
