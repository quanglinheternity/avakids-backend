package com.example.avakids_backend.DTO.Notification;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequest {
    private String title;
    private String message;
    private String topic;
    private String token;
    private Map<String, String> data;
}
