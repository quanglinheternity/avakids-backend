package com.example.avakids_backend.service.Notification;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.avakids_backend.DTO.Notification.NotificationRequest;
import com.example.avakids_backend.entity.UserFcmToken;
import com.example.avakids_backend.enums.NotificationType;
import com.example.avakids_backend.repository.Notification.UserFcmTokenRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmAsyncService {

    private final FCMService fcmService;
    private final UserFcmTokenRepository userFcmTokenRepository;

    @Async
    @Transactional
    public void sendToToken(
            UserFcmToken token, String title, String content, NotificationType type, Map<String, Object> data) {
        try {
            NotificationRequest fcmRequest = new NotificationRequest();
            fcmRequest.setToken(token.getToken());
            fcmRequest.setTitle(title);
            fcmRequest.setMessage(content);

            if (data != null && !data.isEmpty()) {
                Map<String, String> fcmData = new HashMap<>();
                data.forEach((k, v) -> fcmData.put(k, v.toString()));
                fcmRequest.setData(fcmData);
            }

            fcmService.sendMessageToToken(fcmRequest);

            token.setLastUsedAt(LocalDateTime.now());
            userFcmTokenRepository.save(token);

        } catch (Exception e) {
            log.error("Push failed token {}", token.getToken(), e);

            if (e.getMessage() != null
                    && (e.getMessage().contains("not registered")
                            || e.getMessage().contains("invalid"))) {

                token.setIsActive(false);
                userFcmTokenRepository.save(token);
            }
        }
    }
}
