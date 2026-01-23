package com.example.avakids_backend.DTO.Notification;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class RegisterFcmTokenRequest {
    private Long userId;

    @NotBlank(message = "Token không được để trống")
    private String token;

    @NotBlank(message = "Device ID không được để trống")
    private String deviceId;

    @NotBlank(message = "Platform không được để trống")
    private String platform;
}
