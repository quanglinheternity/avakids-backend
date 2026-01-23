package com.example.avakids_backend.service.Notification;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.avakids_backend.DTO.Notification.NotificationResponse;
import com.example.avakids_backend.entity.Notification;
import com.example.avakids_backend.entity.UserFcmToken;
import com.example.avakids_backend.enums.FollowTargetType;
import com.example.avakids_backend.enums.NotificationType;

public interface NotificationService {
    void sendPushNotificationToTopic(String topic, String title, String content, Map<String, Object> data);

    void subscribeUserToOwnTopic(Long userId);

    void subscribeUserToCarTopic(Long userId, FollowTargetType targetType, Long Id);

    void unsubscribeUserFromCarTopic(Long userId, FollowTargetType targetType, Long Id);
    /**
     * Gửi thông báo đến tất cả user đã subscribe một loại thông báo cụ thể
     */
    void sendNotificationByType(NotificationType type, String title, String content, Map<String, Object> data);
    /**
     * Gửi thông báo đến topic của user
     */
    void sendPushNotificationToUserTopic(
            Long userId,
            String title,
            String content,
            NotificationType type,
            Long referenceId,
            Map<String, Object> data);
    /**
     * Gửi thông báo đến tất cả người dùng theo dõi  (qua topic)
     */
    void sendNotificationToCarFollowers(
            Long Id,
            FollowTargetType targetType,
            String title,
            String content,
            NotificationType type,
            Map<String, Object> data);
    /**
     * Đăng ký FCM token và subscribe vào các topic cần thiết
     */
    UserFcmToken registerFcmToken(Long userId, String token, String deviceId, String platform);

    Page<NotificationResponse> getUserNotifications(Long userId, Pageable pageable);

    long getUnreadCount(Long userId);

    Notification markAsRead(Long notificationId, Long userId);

    void markAllAsRead(Long userId);

    void deleteNotification(Long notificationId, Long userId);

    void unregisterFcmToken(String token);

    void unregisterFcmTokenByDevice(Long userId, String deviceId);

    Notification getNotificationById(Long notificationId, Long userId);

    void sendPushNotificationToUser(
            Long userId,
            String title,
            String content,
            NotificationType type,
            Long referenceId,
            Map<String, Object> data);
}
