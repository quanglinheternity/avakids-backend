package com.example.avakids_backend.service.authentication.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.avakids_backend.DTO.Authentication.auth.AuthenticationRequest;
import com.example.avakids_backend.DTO.Authentication.auth.AuthenticationResponse;
import com.example.avakids_backend.DTO.Authentication.introspect.IntrospectRequest;
import com.example.avakids_backend.DTO.Authentication.introspect.IntrospectResponse;
import com.example.avakids_backend.DTO.Authentication.logout.LogoutRequest;
import com.example.avakids_backend.DTO.Authentication.refresh.RefreshRequest;
import com.example.avakids_backend.entity.InvalidatedToken;
import com.example.avakids_backend.entity.User;
import com.example.avakids_backend.enums.RoleType;
import com.example.avakids_backend.exception.AppException;
import com.example.avakids_backend.exception.ErrorCode;
import com.example.avakids_backend.repository.Invalidate.InvalidateRepository;
import com.example.avakids_backend.service.Authentication.auth.AuthenticationServiceImpl;
import com.example.avakids_backend.service.Authentication.auth.AuthenticationValidation;
import com.example.avakids_backend.service.Authentication.token.TokenService;
import com.example.avakids_backend.service.User.UserValidator;
import com.nimbusds.jwt.SignedJWT;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceImplTest {
    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Mock
    private UserValidator userValidator;

    @Mock
    private InvalidateRepository invalidateRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @Mock
    private AuthenticationValidation validation;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setEmail("test@gmail.com");
        user.setPasswordHash("hashed");
        user.setRole(RoleType.ADMIN);
    }

    @Test
    void authenticate_success() {
        AuthenticationRequest request = new AuthenticationRequest("test@gmail.com", "123456");

        when(userValidator.getUserByEmail("test@gmail.com")).thenReturn(user);
        when(passwordEncoder.matches(request.getPassword(), user.getPasswordHash()))
                .thenReturn(true);
        when(tokenService.generateToken(user)).thenReturn("jwt-token");

        AuthenticationResponse response = authenticationService.authenticate(request);

        assertTrue(response.isAuthenticated());
        assertEquals("jwt-token", response.getToken());
    }

    @Test
    void authenticate_invalidRequest_throwException() {
        AuthenticationRequest request = new AuthenticationRequest(null, null);

        doThrow(new AppException(ErrorCode.INVALID_REQUEST)).when(validation).validateLoginRequest(request);

        AppException ex = assertThrows(AppException.class, () -> authenticationService.authenticate(request));

        assertEquals(ErrorCode.INVALID_REQUEST, ex.getErrorCode());
    }

    @Test
    void authenticate_userNotFound_throwException() {
        AuthenticationRequest request = new AuthenticationRequest("notfound@gmail.com", "123");

        when(userValidator.getUserByEmail("notfound@gmail.com")).thenThrow(new AppException(ErrorCode.USER_NOT_FOUND));

        AppException ex = assertThrows(AppException.class, () -> authenticationService.authenticate(request));

        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void authenticate_fail_wrong_password() {
        AuthenticationRequest request = new AuthenticationRequest("test@gmail.com", "wrong-password");

        when(userValidator.getUserByEmail("test@gmail.com")).thenReturn(user);

        when(passwordEncoder.matches(request.getPassword(), user.getPasswordHash()))
                .thenReturn(false);

        AppException exception = assertThrows(AppException.class, () -> authenticationService.authenticate(request));

        assertEquals(ErrorCode.AUTHENTICATION_FAILED, exception.getErrorCode());
    }

    @Test
    void introspect_validToken() throws Exception {
        when(tokenService.isValidToken("token")).thenReturn(true);

        IntrospectResponse response = authenticationService.introspect(new IntrospectRequest("token"));

        assertTrue(response.isValid());
    }

    @Test
    void introspect_invalidToken() throws Exception {
        when(tokenService.isValidToken("bad-token")).thenReturn(false);

        IntrospectResponse response = authenticationService.introspect(new IntrospectRequest("bad-token"));

        assertFalse(response.isValid());
    }

    @Test
    void logout_success() throws Exception {
        SignedJWT jwt = mock(SignedJWT.class);
        var claims = mock(com.nimbusds.jwt.JWTClaimsSet.class);

        when(tokenService.verifyToken("token", true)).thenReturn(jwt);
        when(jwt.getJWTClaimsSet()).thenReturn(claims);
        when(claims.getJWTID()).thenReturn("jwt-id");
        when(claims.getIssueTime()).thenReturn(new Date());

        authenticationService.logout(new LogoutRequest("token"));

        verify(invalidateRepository).save(any(InvalidatedToken.class));
    }

    @Test
    void logout_invalidToken_throwException() throws Exception {
        when(tokenService.verifyToken("bad", true)).thenThrow(new AppException(ErrorCode.UNAUTHORIZED));

        assertThrows(AppException.class, () -> authenticationService.logout(new LogoutRequest("bad")));
    }

    @Test
    void refreshToken_success() throws Exception {
        SignedJWT jwt = mock(SignedJWT.class);
        var claims = mock(com.nimbusds.jwt.JWTClaimsSet.class);

        when(tokenService.verifyToken("old", true)).thenReturn(jwt);
        when(jwt.getJWTClaimsSet()).thenReturn(claims);
        when(claims.getJWTID()).thenReturn("jwt-id");
        when(claims.getExpirationTime()).thenReturn(new Date());
        when(claims.getSubject()).thenReturn("test@gmail.com");

        when(userValidator.getUserByEmail("test@gmail.com")).thenReturn(user);
        when(tokenService.generateToken(user)).thenReturn("new-token");

        AuthenticationResponse response = authenticationService.refreshToken(new RefreshRequest("old"));

        assertEquals("new-token", response.getToken());
        verify(invalidateRepository).save(any(InvalidatedToken.class));
    }

    @Test
    void refreshToken_invalidToken_throwException() throws Exception {
        when(tokenService.verifyToken("expired", true)).thenThrow(new AppException(ErrorCode.UNAUTHORIZED));

        assertThrows(AppException.class, () -> authenticationService.refreshToken(new RefreshRequest("expired")));
    }
}
