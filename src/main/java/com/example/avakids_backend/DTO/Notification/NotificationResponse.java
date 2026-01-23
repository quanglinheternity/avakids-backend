package com.example.avakids_backend.DTO.Notification;

import java.time.LocalDateTime;
import java.util.Map;

import com.example.avakids_backend.enums.NotificationType;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {

    private Long id;
    private String title;
    private String content;

    private NotificationType type;
    private Long referenceId;

    private Map<String, Object> data;

    private Boolean isRead;
    private Boolean isPush;

    private LocalDateTime readAt;
    private LocalDateTime clickedAt;
    private LocalDateTime createdAt;
}
