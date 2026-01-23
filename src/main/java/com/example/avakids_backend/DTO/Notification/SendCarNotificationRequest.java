package com.example.avakids_backend.DTO.Notification;

import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.example.avakids_backend.enums.FollowTargetType;
import com.example.avakids_backend.enums.NotificationType;

import lombok.Data;

@Data
public class SendCarNotificationRequest {

    @NotNull
    private Long carId;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotNull
    private NotificationType type;

    private FollowTargetType targetType;

    // data gửi kèm cho FCM (deep link, screen, ...)
    private Map<String, Object> data;
}
