package com.example.avakids_backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

import com.example.avakids_backend.enums.NotificationType;

import lombok.*;

@Entity
@Table(
        name = "notification",
        indexes = {
            @Index(name = "idx_user_id", columnList = "user_id"),
            @Index(name = "idx_user_read", columnList = "user_id, is_read")
        })
@Getter
@Setter
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NotificationType type;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(columnDefinition = "json")
    private String data; // lưu JSON string

    @Column(name = "is_push")
    private Boolean isPush = false;

    @Column(name = "is_read")
    private Boolean isRead = false;

    private LocalDateTime readAt;
    private LocalDateTime clickedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
