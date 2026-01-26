package com.example.avakids_backend.DTO.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import com.example.avakids_backend.enums.RoleType;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {
    @Email(message = "EMAIL_INVALID")
    private String email;

    @Pattern(regexp = "^(0|\\+84)[0-9]{9}$", message = "PHONE_INVALID")
    private String phone;

    @Size(min = 6, message = "PASSWORD_TOO_SHORT")
    private String password;

    private String fullName;

    private RoleType role;
}
