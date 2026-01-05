package com.example.avakids_backend.service.Authentication.auth;

import org.springframework.stereotype.Component;

import com.example.avakids_backend.DTO.Authentication.auth.AuthenticationRequest;
import com.example.avakids_backend.exception.AppException;
import com.example.avakids_backend.exception.ErrorCode;

@Component
public class AuthenticationValidation {

    public void validateLoginRequest(AuthenticationRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new AppException(ErrorCode.INVALID_USERNAME);
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }
    }
}
