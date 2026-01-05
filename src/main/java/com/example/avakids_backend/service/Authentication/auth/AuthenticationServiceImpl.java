package com.example.avakids_backend.service.Authentication.auth;

import java.text.ParseException;
import java.util.Date;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.avakids_backend.DTO.Authentication.auth.AuthenticationRequest;
import com.example.avakids_backend.DTO.Authentication.auth.AuthenticationResponse;
import com.example.avakids_backend.DTO.Authentication.introspect.IntrospectRequest;
import com.example.avakids_backend.DTO.Authentication.introspect.IntrospectResponse;
import com.example.avakids_backend.DTO.Authentication.logout.LogoutRequest;
import com.example.avakids_backend.DTO.Authentication.refresh.RefreshRequest;
import com.example.avakids_backend.Entity.InvalidatedToken;
import com.example.avakids_backend.Entity.User;
import com.example.avakids_backend.exception.AppException;
import com.example.avakids_backend.exception.ErrorCode;
import com.example.avakids_backend.repository.Invalidate.InvalidateRepository;
import com.example.avakids_backend.repository.User.UserRepository;
import com.example.avakids_backend.service.Authentication.token.TokenService;
import com.example.avakids_backend.service.User.UserValidator;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationServiceImpl implements AuthenticationService {

    UserRepository userRepository;
    UserValidator userValidator;
    InvalidateRepository invalidateRepository;
    PasswordEncoder passwordEncoder;
    TokenService tokenService;
    AuthenticationValidation validation;

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        validation.validateLoginRequest(request);

        User user = userValidator.getUserByEmail(request.getEmail());

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new AppException(ErrorCode.AUTHENTICATION_FAILED);
        }

        String token = tokenService.generateToken(user);
        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }

    @Override
    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        boolean valid = tokenService.isValidToken(request.getToken());
        return IntrospectResponse.builder().valid(valid).build();
    }

    @Override
    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        SignedJWT jwt = tokenService.verifyToken(request.getToken(), true);
        Date issueTime = jwt.getJWTClaimsSet().getIssueTime();
        InvalidatedToken invalidated = InvalidatedToken.builder()
                .id(jwt.getJWTClaimsSet().getJWTID())
                .expiryTime(issueTime)
                .build();

        invalidateRepository.save(invalidated);
    }

    @Override
    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        SignedJWT signedJWT = tokenService.verifyToken(request.getToken(), true);

        String jwtId = signedJWT.getJWTClaimsSet().getJWTID();
        Date expiry = signedJWT.getJWTClaimsSet().getExpirationTime();

        invalidateRepository.save(
                InvalidatedToken.builder().id(jwtId).expiryTime(expiry).build());

        String email = signedJWT.getJWTClaimsSet().getSubject();
        User user = userValidator.getUserByEmail(email);
        ;

        String newToken = tokenService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(newToken)
                .authenticated(true)
                .build();
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        String email = authentication.getName();

        return userValidator.getUserByEmail(email);
    }
}
