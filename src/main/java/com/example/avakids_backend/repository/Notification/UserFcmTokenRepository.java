package com.example.avakids_backend.repository.Notification;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.avakids_backend.entity.UserFcmToken;

public interface UserFcmTokenRepository extends JpaRepository<UserFcmToken, Long> {

    List<UserFcmToken> findByUserIdAndIsActiveTrue(Long userId);

    Optional<UserFcmToken> findByUserIdAndDeviceId(Long userId, String deviceId);

    Optional<UserFcmToken> findByToken(String token);
    Optional<UserFcmToken> findByUserIdAndToken(Long userId,String token);

}
