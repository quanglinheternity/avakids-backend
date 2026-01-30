package com.example.avakids_backend.controller.Authentication;

import java.text.ParseException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.avakids_backend.DTO.ApiResponse;
import com.example.avakids_backend.DTO.Authentication.auth.AuthenticationRequest;
import com.example.avakids_backend.DTO.Authentication.auth.AuthenticationResponse;
import com.example.avakids_backend.DTO.Authentication.introspect.IntrospectRequest;
import com.example.avakids_backend.DTO.Authentication.introspect.IntrospectResponse;
import com.example.avakids_backend.DTO.Authentication.logout.LogoutRequest;
import com.example.avakids_backend.DTO.Authentication.refresh.RefreshRequest;
import com.example.avakids_backend.service.Authentication.auth.AuthenticationService;
import com.example.avakids_backend.util.language.I18n;
import com.nimbusds.jose.JOSEException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "Authentication", description = "APIs for managing authentication")
public class AuthenticationController {
    AuthenticationService authenticationService;
    private final I18n i18n;

    @Operation(summary = "User login")
    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        AuthenticationResponse result = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .message(i18n.t("auth.login.success"))
                .data(result)
                .build();
    }

    @Operation(summary = "Introspect access token")
    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authticated(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .message(i18n.t("auth.introspect.success"))
                .data(result)
                .build();
    }

    @Operation(summary = "Refresh access token")
    @PostMapping("/refresh")
    ApiResponse<AuthenticationResponse> authticated(@RequestBody RefreshRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.refreshToken(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .message(i18n.t("auth.refresh.success"))
                .data(result)
                .build();
    }

    @Operation(summary = "Logout user")
    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ApiResponse.<Void>builder()
                .message(i18n.t("auth.logout.success"))
                .build();
    }
}
