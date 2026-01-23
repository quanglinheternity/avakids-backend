package com.example.avakids_backend.DTO.Notification;

import java.util.Map;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class SendNotificationRequest {
    @NotBlank(message = "Title không được để trống")
    private String title;

    @NotBlank(message = "Content không được để trống")
    private String content;

    private String type;

    private Long referenceId;

    private Map<String, Object> data;
}
