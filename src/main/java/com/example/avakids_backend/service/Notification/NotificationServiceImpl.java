package com.example.avakids_backend.service.Notification;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.avakids_backend.DTO.Notification.NotificationRequest;
import com.example.avakids_backend.DTO.Notification.NotificationResponse;
import com.example.avakids_backend.entity.Follow;
import com.example.avakids_backend.entity.Notification;
import com.example.avakids_backend.entity.UserFcmToken;
import com.example.avakids_backend.enums.FollowTargetType;
import com.example.avakids_backend.enums.NotificationType;
import com.example.avakids_backend.enums.Platform;
import com.example.avakids_backend.exception.AppException;
import com.example.avakids_backend.exception.ErrorCode;
import com.example.avakids_backend.mapper.NotificationMapper;
import com.example.avakids_backend.repository.Notification.FollowRepository;
import com.example.avakids_backend.repository.Notification.NotificationRepository;
import com.example.avakids_backend.repository.Notification.UserFcmTokenRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserFcmTokenRepository userFcmTokenRepository;
    private final FollowRepository followRepository;
    private final FCMService fcmService;
    private final ObjectMapper objectMapper;
    private final FcmAsyncService fcmAsyncService;
    private final NotificationMapper notificationMapper;

    // Topic naming conventions
    private static final String TOPIC_PREFIX_USER = "user_";
    private static final String TOPIC_PREFIX_FOLLOW = "follow_";
    private static final String TOPIC_PREFIX_TYPE = "type_";
    public static final String ADMIN_TOPIC = "admin_all";

    /**
     * Tạo thông báo và lưu vào database
     */
    @Transactional
    public Notification createNotification(
            Long userId,
            String title,
            String content,
            NotificationType type,
            Long referenceId,
            Map<String, Object> data) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setReferenceId(referenceId);

        if (data != null && !data.isEmpty()) {
            try {
                notification.setData(objectMapper.writeValueAsString(data));
            } catch (JsonProcessingException e) {
                log.error("Error converting data to JSON", e);
                notification.setData("{}");
            }
        }

        notification.setIsRead(false);
        notification.setIsPush(false);

        return notificationRepository.save(notification);
    }

    /**
     * Gửi thông báo push notification đến user cụ thể (qua token)
     */
    @Override
    @Transactional
    public void sendPushNotificationToUser(
            Long userId,
            String title,
            String content,
            NotificationType type,
            Long referenceId,
            Map<String, Object> data) {
        try {
            Notification notification = createNotification(userId, title, content, type, referenceId, data);

            List<UserFcmToken> activeTokens = userFcmTokenRepository.findByUserIdAndIsActiveTrue(userId);

            if (activeTokens.isEmpty()) {
                log.info("User {} has no active FCM tokens", userId);
                return;
            }

            sendPushToTokens(activeTokens, title, content, type, data);

            notification.setIsPush(true);
            notificationRepository.save(notification);

            log.info("Sent push notification to user {}: {}", userId, title);

        } catch (Exception e) {
            log.error("Error sending push notification to user {}: {}", userId, e.getMessage());
        }
    }

    /**
     * Gửi thông báo đến một topic
     */
    @Override
    @Transactional
    public void sendPushNotificationToTopic(String topic, String title, String content, Map<String, Object> data) {
        try {
            NotificationRequest fcmRequest = new NotificationRequest();
            fcmRequest.setTopic(topic);
            fcmRequest.setTitle(title);
            fcmRequest.setMessage(content);

            if (data != null && !data.isEmpty()) {
                Map<String, String> fcmData = new HashMap<>();
                data.forEach((key, value) -> fcmData.put(key, value.toString()));
                fcmRequest.setData(fcmData);
            }

            fcmService.sendMessageToTopic(fcmRequest);
            log.info("Sent push notification to topic {}: {}", topic, title);

        } catch (Exception e) {
            log.error("Error sending push notification to topic {}: {}", topic, e.getMessage());
        }
    }

    @Override
    @Transactional
    public void sendPushNotificationToUserTopic(
            Long userId,
            String title,
            String content,
            NotificationType type,
            Long referenceId,
            Map<String, Object> data) {
        String userTopic = getUserTopic(userId);

        // 1. Tạo thông báo trong database cho user
        createNotification(userId, title, content, type, referenceId, data);

        // 2. Gửi đến topic của user
        sendPushNotificationToTopic(userTopic, title, content, data);

        log.info("Sent push notification to user topic {}: {}", userTopic, title);
    }

    @Override
    @Transactional
    public void sendNotificationToCarFollowers(
            Long Id,
            FollowTargetType targetType,
            String title,
            String content,
            NotificationType type,
            Map<String, Object> data) {
        String topic = getTopic(Id, targetType);

        List<Follow> followers = followRepository.findFollowers(targetType, Id);
        if (followers.isEmpty()) {
            return;
        }
        log.info("Sending notification to {} followers of car {} via topic {}", followers.size(), Id, topic);

        sendPushNotificationToTopic(topic, title, content, data);

        // 3. Lưu thông báo cho từng user trong database
        for (Follow follow : followers) {
            createNotification(follow.getUserId(), title, content, type, Id, data);
        }
    }

    @Override
    @Transactional
    public void sendNotificationByType(NotificationType type, String title, String content, Map<String, Object> data) {
        String typeTopic = getTypeTopic(type);
        sendPushNotificationToTopic(typeTopic, title, content, data);
        log.info("Sent notification to type topic {}: {}", typeTopic, title);
    }

    /**
     * Subscribe user vào topic của một xe
     */
    @Override
    @Transactional
    public void subscribeUserToCarTopic(Long userId, FollowTargetType targetType, Long Id) {
        List<UserFcmToken> tokens = getActiveTokens(userId);
        if (tokens.isEmpty()) return;

        String topic = getTopic(Id, targetType);
        List<String> tokenStrings = tokens.stream().map(UserFcmToken::getToken).collect(Collectors.toList());

        try {
            fcmService.subscribeMultipleToTopic(tokenStrings, topic);
            log.info("Subscribed user {} to  topic {}", userId, topic);
        } catch (FirebaseMessagingException e) {
            log.error("Error subscribing user {} to  topic {}: {}", userId, topic, e.getMessage());
        }
    }

    /**
     * Unsubscribe user khỏi topic của một xe
     */
    @Override
    @Transactional
    public void unsubscribeUserFromCarTopic(Long userId, FollowTargetType targetType, Long Id) {
        List<UserFcmToken> tokens = getActiveTokens(userId);
        if (tokens.isEmpty()) return;

        String topic = getTopic(Id, targetType);
        List<String> tokenStrings = tokens.stream().map(UserFcmToken::getToken).collect(Collectors.toList());

        try {
            fcmService.unsubscribeMultipleFromTopic(tokenStrings, topic);
            log.info("Unsubscribed user {} from car topic {}", userId, topic);
        } catch (FirebaseMessagingException e) {
            log.error("Error unsubscribing user {} from car topic {}: {}", userId, topic, e.getMessage());
        }
    }

    /**
     * Subscribe user vào topic của chính họ (để nhận thông báo cá nhân)
     */
    @Override
    @Transactional
    public void subscribeUserToOwnTopic(Long userId) {
        List<UserFcmToken> tokens = getActiveTokens(userId);
        if (tokens.isEmpty()) return;

        String userTopic = getUserTopic(userId);
        List<String> tokenStrings = tokens.stream().map(UserFcmToken::getToken).collect(Collectors.toList());

        try {
            fcmService.subscribeMultipleToTopic(tokenStrings, userTopic);
            log.info("Subscribed user {} to own topic {}", userId, userTopic);
        } catch (FirebaseMessagingException e) {
            log.error("Error subscribing user {} to own topic {}: {}", userId, userTopic, e.getMessage());
        }
    }

    /**
     * Đăng ký FCM token và subscribe vào các topic cần thiết
     */
    @Override
    @Transactional
    public UserFcmToken registerFcmToken(Long userId, String token, String deviceId, String platform) {
        log.info("người dùng {}", userId);
        Optional<UserFcmToken> existingToken = userFcmTokenRepository.findByToken(token);
        if (existingToken.isPresent()) {
            UserFcmToken userToken = existingToken.get();
            if (!Objects.equals(userToken.getUserId(), userId)) {
                userToken.setUserId(userId);
            }
            userToken.setLastUsedAt(LocalDateTime.now());
            userToken.setIsActive(true);
            userFcmTokenRepository.save(userToken);

            subscribeTokenToUserTopics(userId, token);
            return userToken;
        }

        Optional<UserFcmToken> existingDeviceToken = userFcmTokenRepository.findByUserIdAndDeviceId(userId, deviceId);

        if (existingDeviceToken.isPresent()) {
            UserFcmToken entity = existingDeviceToken.get();

            entity.setToken(token);
            entity.setPlatform(Platform.valueOf(platform.toUpperCase()));
            entity.setIsActive(true);
            entity.setLastUsedAt(LocalDateTime.now());

            UserFcmToken saved = userFcmTokenRepository.save(entity);
            subscribeTokenToUserTopics(userId, token);
            return saved;
        }

        UserFcmToken newToken = new UserFcmToken();
        newToken.setUserId(userId);
        newToken.setToken(token);
        newToken.setDeviceId(deviceId);
        newToken.setPlatform(com.example.avakids_backend.enums.Platform.valueOf(platform.toUpperCase()));
        newToken.setIsActive(true);
        newToken.setLastUsedAt(LocalDateTime.now());

        UserFcmToken savedToken = userFcmTokenRepository.save(newToken);

        subscribeTokenToUserTopics(userId, token);

        return savedToken;
    }

    /**
     * Subscribe token vào các topic của user
     */
    private void subscribeTokenToUserTopics(Long userId, String token) {
        try {
            //            User user = userValidator.validateUserExists(userId);
            // Subscribe vào topic cá nhân của user
            String userTopic = getUserTopic(userId);
            fcmService.subscribeToTopic(token, userTopic);
            //            if (user.getRole() == Role.ADMIN) {
            //                fcmService.subscribeToTopic(token, ADMIN_TOPIC);
            //            }

            // Subscribe vào các topic xe mà user đang theo dõi
            List<Follow> follows = followRepository.findByUserId(userId);
            for (Follow follow : follows) {
                if (follow.getNotify() != null && follow.getNotify()) {
                    String carTopic = getTopic(follow.getTargetId(), follow.getTargetType());
                    fcmService.subscribeToTopic(token, carTopic);
                }
            }

            log.info("Token subscribed to user {} topics", userId);
        } catch (FirebaseMessagingException e) {
            log.error("Error subscribing token to user topics: {}", e.getMessage());
        }
    }

    /**
     * Cập nhật cài đặt notify cho car follow và quản lý subscription
     */
    @Transactional
    public void updateCarFollowNotify(Long userId, FollowTargetType targetType, Long carId, Boolean notify) {
        boolean newNotify = Boolean.TRUE.equals(notify);
        Follow carFollow = followRepository
                .findByUserAndTarget(userId, targetType, carId)
                .orElseGet(() -> {
                    Follow cf = new Follow();
                    cf.setUserId(userId);
                    cf.setTargetType(targetType);
                    cf.setTargetId(carId);
                    cf.setNotify(notify); // mặc định chưa notify
                    return cf;
                });

        boolean oldNotify = Boolean.TRUE.equals(carFollow.getNotify());

        if (oldNotify == newNotify) {
            return;
        }
        if (newNotify) {
            subscribeUserToCarTopic(userId, targetType, carId);
        } else {
            unsubscribeUserFromCarTopic(userId, targetType, carId);
        }

        carFollow.setNotify(newNotify);
        followRepository.save(carFollow);
    }

    private String getUserTopic(Long userId) {
        return TOPIC_PREFIX_USER + userId;
    }

    private String getTopic(Long Id, FollowTargetType targetType) {
        return TOPIC_PREFIX_FOLLOW + targetType + "_" + Id;
    }

    private String getTypeTopic(NotificationType type) {
        return TOPIC_PREFIX_TYPE + type.name().toLowerCase();
    }

    private void sendPushToTokens(
            List<UserFcmToken> tokens, String title, String content, NotificationType type, Map<String, Object> data) {

        for (UserFcmToken token : tokens) {
            fcmAsyncService.sendToToken(token, title, content, type, data);
        }
    }

    @Override
    public Page<NotificationResponse> getUserNotifications(Long userId, Pageable pageable) {
        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(notificationMapper::toResponse);
    }

    @Override
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Override
    @Transactional
    public Notification markAsRead(Long notificationId, Long userId) {
        Notification notification = getNotificationById(notificationId, userId);

        if (!notification.getIsRead()) {
            notification.setIsRead(true);
            notification.setReadAt(LocalDateTime.now());
            notification = notificationRepository.save(notification);
        }

        return notification;
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndIsReadFalse(userId);

        LocalDateTime now = LocalDateTime.now();
        unreadNotifications.forEach(notification -> {
            notification.setIsRead(true);
            notification.setReadAt(now);
        });

        notificationRepository.saveAll(unreadNotifications);
    }

    @Override
    @Transactional
    public void deleteNotification(Long notificationId, Long userId) {
        Notification notification = getNotificationById(notificationId, userId);

        notificationRepository.delete(notification);
    }

    @Override
    @Transactional
    public void unregisterFcmToken(String token) {
        userFcmTokenRepository.findByToken(token).ifPresent(userToken -> {
            userToken.setIsActive(false);
            userFcmTokenRepository.save(userToken);
        });
    }

    @Override
    @Transactional
    public void unregisterFcmTokenByDevice(Long userId, String deviceId) {
        userFcmTokenRepository.findByUserIdAndDeviceId(userId, deviceId).ifPresent(userToken -> {
            userToken.setIsActive(false);
            userFcmTokenRepository.save(userToken);
        });
    }

    public Notification getNotificationById(Long notificationId, Long userId) {
        return notificationRepository
                .findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));
    }

    private List<UserFcmToken> getActiveTokens(Long userId) {
        return userFcmTokenRepository.findByUserIdAndIsActiveTrue(userId);
    }
}
