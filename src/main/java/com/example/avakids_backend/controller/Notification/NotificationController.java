package com.example.avakids_backend.controller.Notification;

import com.example.avakids_backend.DTO.Authentication.introspect.IntrospectRequest;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.example.avakids_backend.DTO.ApiResponse;
import com.example.avakids_backend.DTO.Notification.*;
import com.example.avakids_backend.entity.Notification;
import com.example.avakids_backend.entity.UserFcmToken;
import com.example.avakids_backend.enums.FollowTargetType;
import com.example.avakids_backend.service.Notification.NotificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
@Tag(name = "Notification Management", description = "APIs for managing notifications & FCM")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(
            summary = "Subscribe user to personal topic",
            description = "Subscribe the authenticated user to their own personal notification topic")
    @PostMapping("/subscribe/user-topic")
    public ApiResponse<Void> subscribeUserTopic(@AuthenticationPrincipal Long userId) {
        notificationService.subscribeUserToOwnTopic(userId);
        return ApiResponse.<Void>builder()
                .message("Đã subscribe vào topic cá nhân")
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
                .message("Đã subscribe vào topic của xe")
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
                .message("Đã unsubscribe khỏi topic của xe")
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
                .message("Đã gửi thông báo đến topic: " + topic)
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
                .message("Đã gửi thông báo theo type: " + type)
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

        return ApiResponse.<Void>builder().message("Đã gửi thông báo đến user").build();
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
                .message("Đã gửi thông báo tới followers")
                .build();
    }

    @Operation(
            summary = "Register FCM token",
            description = "Register an FCM token for a user and device to receive push notifications")
    @PostMapping("/fcm/register")
    public ApiResponse<UserFcmToken> registerFcm(@Valid @RequestBody RegisterFcmTokenRequest request) {
        return ApiResponse.<UserFcmToken>builder()
                .message("Đăng ký FCM token thành công")
                .data(notificationService.registerFcmToken(
                        request.getUserId(), request.getToken(), request.getDeviceId(), request.getPlatform()))
                .build();
    }

    @Operation(
            summary = "Get user notifications",
            description = "Retrieve a paginated list of notifications belonging to a specific user")
    @GetMapping("/list")
    public ApiResponse<Page<NotificationResponse>> getUserNotifications(
            @RequestParam Long userId, @PageableDefault(size = 20) Pageable pageable) {

        return ApiResponse.<Page<NotificationResponse>>builder()
                .message("Lấy danh sách notification thành công")
                .data(notificationService.getUserNotifications(userId, pageable))
                .build();
    }

    @Operation(
            summary = "Get unread notification count",
            description = "Get the total number of unread notifications for a specific user")
    @GetMapping("/unread-count")
    public ApiResponse<Long> unreadCount(@RequestParam Long userId) {
        return ApiResponse.<Long>builder()
                .message("Lấy số notification chưa đọc")
                .data(notificationService.getUnreadCount(userId))
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
    public ApiResponse<Notification> markRead(@PathVariable Long id, @RequestParam Long userId) {

        return ApiResponse.<Notification>builder()
                .message("Đã đánh dấu đã đọc")
                .data(notificationService.markAsRead(id, userId))
                .build();
    }

    @Operation(
            summary = "Mark all notifications as read",
            description = "Mark all notifications of a specific user as read")
    @PutMapping("/read-all")
    public ApiResponse<Void> markAllRead(@RequestParam Long userId) {
        notificationService.markAllAsRead(userId);
        return ApiResponse.<Void>builder()
                .message("Đã đánh dấu tất cả notification đã đọc")
                .build();
    }

    @Operation(
            summary = "Delete a notification",
            description = "Delete a specific notification that belongs to the given user")
    @DeleteMapping("/{id}/delete")
    public ApiResponse<Void> delete(@PathVariable Long id, @RequestParam Long userId) {

        notificationService.deleteNotification(id, userId);
        return ApiResponse.<Void>builder()
                .message("Xóa notification thành công")
                .build();
    }

    @Operation(
            summary = "Unregister FCM token",
            description = "Remove an FCM token when the user logs out or disables push notifications")
    @PostMapping("/fcm/unregister")
    public ApiResponse<Void> unregisterFcmToken(@RequestBody IntrospectRequest request) {
        notificationService.unregisterFcmToken(request.getToken());
        return ApiResponse.<Void>builder()
                .message("Mã thông báo FCM đã được hủy đăng ký thành công.")
                .build();
    }

    @Operation(
            summary = "Unregister FCM token by device",
            description = "Remove an FCM token associated with a specific device of a user")
    @PostMapping("/fcm/unregister-device")
    public ApiResponse<Void> unregisterFcmTokenByDevice(@RequestParam Long userId, @RequestParam String deviceId) {
        notificationService.unregisterFcmTokenByDevice(userId, deviceId);
        return ApiResponse.<Void>builder()
                .message("Mã thông báo FCM đã được hủy đăng ký thành công cho thiết bị.")
                .build();
    }
}
