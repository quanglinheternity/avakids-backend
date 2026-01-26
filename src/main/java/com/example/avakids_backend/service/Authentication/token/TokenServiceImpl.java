package com.example.avakids_backend.service.Authentication.token;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.avakids_backend.entity.User;
import com.example.avakids_backend.exception.AppException;
import com.example.avakids_backend.exception.ErrorCode;
import com.example.avakids_backend.repository.Invalidate.InvalidateRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jwt.*;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenServiceImpl implements TokenService {

    final InvalidateRepository invalidateRepository;

    @Value("${jwt.signerKey}")
    String SIGNER_KEY;

    @Value("${jwt.vaild-duration}")
    Long VALID_DURATION;

    @Value("${jwt.refresh-duration}")
    Long REFRESH_DURATION;

    @Override
    public String generateToken(User user) {
        log.info("signerKey: {} ", SIGNER_KEY);
        try {
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(user.getEmail())
                    .issuer("com.transport")
                    .issueTime(new Date())
                    .expirationTime(Date.from(Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS)))
                    .jwtID(UUID.randomUUID().toString())
                    .claim("scope", buildScope(user))
                    .build();

            JWSObject jwsObject = new JWSObject(header, new Payload(claims.toJSONObject()));
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Error generating token", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public SignedJWT verifyToken(String token, boolean isRefresh) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        Date expiry = isRefresh
                ? Date.from(signedJWT
                        .getJWTClaimsSet()
                        .getExpirationTime()
                        .toInstant()
                        .plus(REFRESH_DURATION, ChronoUnit.SECONDS))
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        boolean verified = signedJWT.verify(verifier);
        if (!(verified && expiry.after(new Date()))) {
            throw new AppException(isRefresh ? ErrorCode.TOKEN_EXPIRED : ErrorCode.AUTHENTICATION_FAILED);
        }

        if (invalidateRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new AppException(ErrorCode.TOKEN_EXPIRED);
        }

        return signedJWT;
    }

    @Override
    public boolean isValidToken(String token) throws JOSEException, ParseException {
        try {
            verifyToken(token, false);
            return true;
        } catch (AppException | ParseException | JOSEException e) {
            log.warn("Invalid token: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Unexpected error validating token", e);
            return false;
        }
    }

    private String buildScope(User user) {
        if (user.getRole() == null) {
            return "";
        }
        return "ROLE_" + user.getRole().name();
    }
}
