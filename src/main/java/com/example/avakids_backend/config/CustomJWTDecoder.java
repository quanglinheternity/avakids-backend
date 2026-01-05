package com.example.avakids_backend.config;


import java.text.ParseException;
import javax.crypto.spec.SecretKeySpec;

import com.example.avakids_backend.DTO.Authentication.introspect.IntrospectRequest;
import com.example.avakids_backend.service.Authentication.auth.AuthenticationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.JOSEException;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomJWTDecoder implements JwtDecoder {

    @Value("${jwt.signerKey}")
    private String signerKey;

    private final @Lazy AuthenticationService authenticationService;

    private NimbusJwtDecoder nimbusJwtDecoder;

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            // introspect trước khi decode
            var introspectResponse = authenticationService.introspect(
                    IntrospectRequest.builder().token(token).build());
            if (!introspectResponse.isValid()) throw new JwtException("Token introspection failed");
        } catch (JOSEException | ParseException e) {
            log.error("Introspection error for token: {}", token, e);
            throw new JwtException("Token introspection failed: " + e.getMessage(), e);
        }

        if (nimbusJwtDecoder == null) {
            SecretKeySpec secretKey = new SecretKeySpec(signerKey.getBytes(), "HmacSHA512");
            nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKey)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }

        return nimbusJwtDecoder.decode(token);
    }
}
