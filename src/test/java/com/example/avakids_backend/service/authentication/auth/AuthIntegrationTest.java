package com.example.avakids_backend.service.authentication.auth;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.avakids_backend.DTO.Authentication.introspect.IntrospectRequest;
import com.example.avakids_backend.DTO.Authentication.logout.LogoutRequest;
import com.example.avakids_backend.DTO.Authentication.refresh.RefreshRequest;
import com.example.avakids_backend.repository.User.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.example.avakids_backend.DTO.Authentication.auth.AuthenticationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        private String accessToken;

        @BeforeEach
        void setup() throws Exception {
    //        User user = new User();
    //        user.setEmail("admin@gamil.com");
    //        user.setPasswordHash(passwordEncoder.encode("123456"));
    //        user.setRole(RoleType.ADMIN);
    //        userRepository.save(user);
        AuthenticationRequest loginReq = new AuthenticationRequest();
            loginReq.setEmail("admin@gamil.com");
            loginReq.setPassword("123456");

        String response = mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginReq))
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        accessToken = objectMapper
                .readTree(response)
                .get("data")
                    .get("token")
                    .asText();
        }
    @Test
    void login_success() throws Exception {
        // given
        AuthenticationRequest request = new AuthenticationRequest();
        request.setEmail("admin@gamil.com");
        request.setPassword("123456");

        // when & then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.authenticated").value(true));
    }

    @Test
    void login_fail_wrong_password() throws Exception {
        // given
        AuthenticationRequest request = new AuthenticationRequest();
        request.setEmail("admin@gamil.com");
        request.setPassword("wrong-password");

        // when & then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(2001))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void login_fail_email_not_found() throws Exception {
        // given
        AuthenticationRequest request = new AuthenticationRequest();
        request.setEmail("not-exist@gmail.com");
        request.setPassword("123456");

        // when & then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(3000))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void login_fail_missing_password() throws Exception {
        // given
        AuthenticationRequest request = new AuthenticationRequest();
        request.setEmail("admin@gamil.com");

        // when & then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(2004))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").doesNotExist());
    }
    @Test
    void introspect_valid_token() throws Exception {
        IntrospectRequest request = new IntrospectRequest();
        request.setToken(accessToken);

        mockMvc.perform(
                        post("/api/v1/auth/introspect")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data.valid").value(true));
    }
    @Test
    void logout_success() throws Exception {
        LogoutRequest request = new LogoutRequest();
        request.setToken(accessToken);

        mockMvc.perform(
                        post("/api/v1/auth/logout")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk());
    }
    @Test
    void introspect_after_logout_should_be_invalid() throws Exception {
        // logout trước
        LogoutRequest logoutReq = new LogoutRequest();
        logoutReq.setToken(accessToken);

        mockMvc.perform(
                        post("/api/v1/auth/logout")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(logoutReq))
                )
                .andExpect(status().isOk());

        // introspect lại
        IntrospectRequest introspectReq = new IntrospectRequest();
        introspectReq.setToken(accessToken);

        mockMvc.perform(
                        post("/api/v1/auth/introspect")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(introspectReq))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data.valid").value(false));
    }
    @Test
    void refresh_token_success() throws Exception {
        RefreshRequest request = new RefreshRequest();
        request.setToken(accessToken);

        String response = mockMvc.perform(
                        post("/api/v1/auth/refresh")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1000))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.authenticated").value(true))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String newToken = objectMapper
                .readTree(response)
                .get("data")
                .get("token")
                .asText();

        assertThat(newToken).isNotEqualTo(accessToken);
    }
    @Test
    void old_token_should_be_invalid_after_refresh() throws Exception {
        // refresh trước
        RefreshRequest refreshReq = new RefreshRequest();
        refreshReq.setToken(accessToken);

        mockMvc.perform(
                        post("/api/v1/auth/refresh")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(refreshReq))
                )
                .andExpect(status().isOk());

        // introspect lại token cũ
        IntrospectRequest introspectReq = new IntrospectRequest();
        introspectReq.setToken(accessToken);

        mockMvc.perform(
                        post("/api/v1/auth/introspect")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(introspectReq))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.valid").value(false));
    }
    @Test
    void refresh_with_invalidated_token_should_fail() throws Exception {
        RefreshRequest request = new RefreshRequest();
        request.setToken(accessToken);

        mockMvc.perform(
                post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk());

        mockMvc.perform(
                        post("/api/v1/auth/refresh")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isUnauthorized());
    }

}