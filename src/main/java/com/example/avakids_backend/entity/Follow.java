package com.example.avakids_backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

import com.example.avakids_backend.enums.FollowTargetType;

import lombok.*;

@Entity
@Table(
        name = "follow",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "target_type", "target_id"})},
        indexes = {
            @Index(name = "idx_target", columnList = "target_type,target_id"),
            @Index(name = "idx_user", columnList = "user_id")
        })
@Getter
@Setter
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private FollowTargetType targetType;

    private Long targetId;

    private Boolean notify = true;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
