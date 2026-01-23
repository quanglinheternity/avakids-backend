package com.example.avakids_backend.repository.Notification;

import java.util.List;
import java.util.Optional;

import com.example.avakids_backend.entity.Follow;
import com.example.avakids_backend.enums.FollowTargetType;

public interface FollowRepositoryCustom {
    List<Follow> findFollowers(FollowTargetType targetType, Long targetId);

    Optional<Follow> findByUserAndTarget(Long userId, FollowTargetType targetType, Long targetId);
}
