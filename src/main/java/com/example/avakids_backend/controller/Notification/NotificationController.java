package com.example.avakids_backend.controller.Notification;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.example.avakids_backend.DTO.ApiResponse;
import com.example.avakids_backend.DTO.Authentication.introspect.IntrospectRequest;
import com.example.avakids_backend.DTO.Notification.*;
import com.example.avakids_backend.entity.Notification;
import com.example.avakids_backend.entity.UserFcmToken;
import com.example.avakids_backend.enums.FollowTargetType;
import com.example.avakids_backend.service.Notification.NotificationService;
import com.example.avakids_backend.util.language.I18n;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
@Tag(name = "Notification Management", description = "APIs for managing notifications & FCM")
public class NotificationController {

    private final NotificationService notificationService;
    private final I18n i18n;

    @Operation(
            summary = "Subscribe user to personal topic",
            description = "Subscribe the authenticated user to their own personal notification topic")
    @PostMapping("/subscribe/user-topic")
    public ApiResponse<Void> subscribeUserTopic(@AuthenticationPrincipal Long userId) {
        notificationService.subscribeUserToOwnTopic(userId);
        return ApiResponse.<Void>builder()
                .message(i18n.t("notification.subscribe.user"))
                .build();
    }

    @Operation(
            summary = "Subscribe to follow topic",
            description =
                    "Subscribe the authenticated user to a follow topic in order to receive notifications related to that follow")
    @PostMapping("/subscribe/follow/{followId}")
    public ApiResponse<Void> subscribeCarTopic(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long followId,
            @RequestParam FollowTargetType targetType) {

        notificationService.subscribeUserToCarTopic(userId, targetType, followId);
        return ApiResponse.<Void>builder()
                .message(i18n.t("notification.subscribe.follow"))
                .build();
    }

    @Operation(
            summary = "Unsubscribe from car topic",
            description = "Unsubscribe the authenticated user from a specific car notification topic")
    @PostMapping("/unsubscribe/car/{carId}")
    public ApiResponse<Void> unsubscribeCarTopic(
            @AuthenticationPrincipal Long userId, @PathVariable Long carId, @RequestParam FollowTargetType targetType) {

        notificationService.unsubscribeUserFromCarTopic(userId, targetType, carId);
        return ApiResponse.<Void>builder()
                .message(i18n.t("notification.unsubscribe.follow"))
                .build();
    }

    @Operation(
            summary = "Send notification to topic (Admin)",
            description = "Send a push notification to all users subscribed to a specific topic. Admin access only.")
    @PostMapping("/admin/topic/{topic}")
    public ApiResponse<Void> sendToTopic(
            @PathVariable String topic, @Valid @RequestBody SendNotificationRequest request) {

        notificationService.sendPushNotificationToTopic(
                topic, request.getTitle(), request.getContent(), request.getData());

        return ApiResponse.<Void>builder()
                .message(i18n.t("notification.send.topic", topic))
                .build();
    }

    @Operation(
            summary = "Send notification by type (Admin)",
            description =
                    "Send a push notification to all users subscribed to a specific notification type. Admin access only.")
    @PostMapping("/admin/type/{type}")
    public ApiResponse<Void> sendByType(
            @PathVariable String type, @Valid @RequestBody SendNotificationRequest request) {

        notificationService.sendNotificationByType(
                com.example.avakids_backend.enums.NotificationType.valueOf(type),
                request.getTitle(),
                request.getContent(),
                request.getData());

        return ApiResponse.<Void>builder()
                .message(i18n.t("notification.send.type", type))
                .build();
    }

    @Operation(
            summary = "Send notification to user (Admin)",
            description = "Send a push notification to a specific user's personal topic. Admin access only.")
    @PostMapping("/admin/user/{userId}")
    public ApiResponse<Void> sendToUser(
            @PathVariable Long userId, @Valid @RequestBody SendNotificationRequest request) {

        notificationService.sendPushNotificationToUserTopic(
                userId,
                request.getTitle(),
                request.getContent(),
                com.example.avakids_backend.enums.NotificationType.valueOf(request.getType()),
                request.getReferenceId(),
                request.getData());

        return ApiResponse.<Void>builder()
                .message(i18n.t("notification.send.user"))
                .build();
    }

    @Operation(
            summary = "Send notification to  followers",
            description = "Send a notification to all users who are following a specific ")
    @PostMapping("/followers")
    public ApiResponse<Void> notifyFollowers(@Valid @RequestBody SendCarNotificationRequest request) {
        notificationService.sendNotificationToCarFollowers(
                request.getCarId(),
                request.getTargetType(),
                request.getTitle(),
                request.getContent(),
                request.getType(),
                request.getData());

        return ApiResponse.<Void>builder()
                .message(i18n.t("notification.send.followers"))
                .build();
    }

    @Operation(
            summary = "Register FCM token",
            description = "Register an FCM token for a user and device to receive push notifications")
    @PostMapping("/fcm/register")
    public ApiResponse<UserFcmToken> registerFcm(@Valid @RequestBody RegisterFcmTokenRequest request) {
        return ApiResponse.<UserFcmToken>builder()
                .message(i18n.t("notification.fcm.register.success"))
                .data(notificationService.registerFcmToken(
                        request.getUserId(), request.getToken(), request.getDeviceId(), request.getPlatform()))
                .build();
    }

    @Operation(
            summary = "Get user notifications",
            description = "Retrieve a paginated list of notifications belonging to a specific user")
    @GetMapping("/list")
    public ApiResponse<Page<NotificationResponse>> getUserNotifications(
           @PageableDefault(size = 20) Pageable pageable) {

        return ApiResponse.<Page<NotificationResponse>>builder()
                .message(i18n.t("notification.list.success"))
                .data(notificationService.getUserNotifications(pageable))
                .build();
    }

    @Operation(
            summary = "Get unread notification count",
            description = "Get the total number of unread notifications for a specific user")
    @GetMapping("/unread-count")
    public ApiResponse<Long> unreadCount() {
        return ApiResponse.<Long>builder()
                .message(i18n.t("notification.unread.count.success"))
                .data(notificationService.getUnreadCount())
                .build();
    }

    @Operation(
            summary = "Get notification detail",
            description = "Retrieve detailed information of a specific notification by its ID")
    @GetMapping("/{id}/detail")
    public ResponseEntity<Notification> getNotificationById(
            @PathVariable("id") Long notificationId, @RequestParam Long userId) {
        return ResponseEntity.ok(notificationService.getNotificationById(notificationId, userId));
    }

    @Operation(
            summary = "Mark notification as read",
            description = "Mark a specific notification as read for the given user")
    @PutMapping("/{id}/read")
    public ApiResponse<Notification> markRead(@PathVariable Long id) {

        return ApiResponse.<Notification>builder()
                .message(i18n.t("notification.read.success"))
                .data(notificationService.markAsRead(id))
                .build();
    }

    @Operation(
            summary = "Mark all notifications as read",
            description = "Mark all notifications of a specific user as read")
    @PutMapping("/read-all")
    public ApiResponse<Void> markAllRead() {
        notificationService.markAllAsRead();
        return ApiResponse.<Void>builder()
                .message(i18n.t("notification.read.all.success"))
                .build();
    }

    @Operation(
            summary = "Delete a notification",
            description = "Delete a specific notification that belongs to the given user")
    @DeleteMapping("/{id}/delete")
    public ApiResponse<Void> delete(@PathVariable Long id, @RequestParam Long userId) {

        notificationService.deleteNotification(id, userId);
        return ApiResponse.<Void>builder()
                .message(i18n.t("notification.delete.success"))
                .build();
    }

    @Operation(
            summary = "Unregister FCM token",
            description = "Remove an FCM token when the user logs out or disables push notifications")
    @PostMapping("/fcm/unregister")
    public ApiResponse<Void> unregisterFcmToken(@RequestBody IntrospectRequest request) {
        notificationService.unregisterFcmToken(request.getToken());
        return ApiResponse.<Void>builder()
                .message(i18n.t("notification.fcm.unregister.success"))
                .build();
    }

    @Operation(
            summary = "Unregister FCM token by device",
            description = "Remove an FCM token associated with a specific device of a user")
    @PostMapping("/fcm/unregister-device")
    public ApiResponse<Void> unregisterFcmTokenByDevice(@RequestParam Long userId, @RequestParam String deviceId) {
        notificationService.unregisterFcmTokenByDevice(userId, deviceId);
        return ApiResponse.<Void>builder()
                .message(i18n.t("notification.fcm.unregister.device.success"))
                .build();
    }
}
