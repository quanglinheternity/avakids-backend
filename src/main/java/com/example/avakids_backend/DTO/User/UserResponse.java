package com.example.avakids_backend.DTO.User;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    private Long id;
    private String email;
    private String phone;
    private String fullName;
    private String avatarUrl;
    private LocalDateTime emailVerifiedAt;
    private LocalDateTime createdAt;
}
