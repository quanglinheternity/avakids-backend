package com.example.avakids_backend.service.Authentication.token;

import java.text.ParseException;

import com.example.avakids_backend.entity.User;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;

public interface TokenService {
    String generateToken(User user);

    SignedJWT verifyToken(String token, boolean isRefresh) throws ParseException, JOSEException;

    boolean isValidToken(String token) throws JOSEException, ParseException;
}
