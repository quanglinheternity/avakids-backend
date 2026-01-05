package com.example.avakids_backend.config;

import com.example.avakids_backend.DTO.ApiResponse;
import com.example.avakids_backend.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override // ghi đè
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        ErrorCode errorCode = ErrorCode.AUTHENTICATION_REQUIRED;
        response.setStatus(errorCode.getHttpStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        response.flushBuffer();
    }
}
