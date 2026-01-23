package com.example.avakids_backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.example.avakids_backend.enums.Platform;

import lombok.*;

@Entity
@Table(
        name = "user_fcm_token",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = "token"),
            @UniqueConstraint(columnNames = {"user_id", "device_id"})
        },
        indexes = {@Index(name = "idx_user_id", columnList = "user_id")})
@Getter
@Setter
public class UserFcmToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(length = 500, nullable = false)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Platform platform;

    @Column(name = "device_id", length = 100, nullable = false)
    private String deviceId;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
