package com.example.avakids_backend.service.Notification;

import java.time.Duration;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.avakids_backend.DTO.Notification.NotificationRequest;
import com.google.firebase.messaging.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Service
public class FCMService {
    private final Logger logger = LoggerFactory.getLogger(FCMService.class);

    /**
     * Gửi message đến một device token cụ thể
     */
    public void sendMessageToToken(NotificationRequest request) throws InterruptedException, ExecutionException {
        Message message = buildMessage(request);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonOutput = gson.toJson(message);
        String response = FirebaseMessaging.getInstance().sendAsync(message).get();
        logger.info(
                "Sent message to token. Device token: " + request.getToken() + ", " + response + " msg " + jsonOutput);
    }

    /**
     * Gửi message đến một topic
     */
    public void sendMessageToTopic(NotificationRequest request) throws InterruptedException, ExecutionException {
        Message message = buildMessageForTopic(request);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonOutput = gson.toJson(message);
        String response = FirebaseMessaging.getInstance().sendAsync(message).get();
        logger.info("Sent message to topic: " + request.getTopic() + ", " + response + " msg " + jsonOutput);
    }

    /**
     * Subscribe device token vào topic
     */
    public void subscribeToTopic(String token, String topic) throws FirebaseMessagingException {
        FirebaseMessaging.getInstance().subscribeToTopic(java.util.Arrays.asList(token), topic);
        logger.info("Subscribed token to topic: {} - {}", token, topic);
    }

    /**
     * Unsubscribe device token khỏi topic
     */
    public void unsubscribeFromTopic(String token, String topic) throws FirebaseMessagingException {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(java.util.Arrays.asList(token), topic);
        logger.info("Unsubscribed token from topic: {} - {}", token, topic);
    }

    /**
     * Subscribe nhiều tokens vào topic
     */
    public void subscribeMultipleToTopic(java.util.List<String> tokens, String topic)
            throws FirebaseMessagingException {
        FirebaseMessaging.getInstance().subscribeToTopic(tokens, topic);
        logger.info("Subscribed {} tokens to topic: {}", tokens.size(), topic);
    }

    /**
     * Unsubscribe nhiều tokens khỏi topic
     */
    public void unsubscribeMultipleFromTopic(java.util.List<String> tokens, String topic)
            throws FirebaseMessagingException {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(tokens, topic);
        logger.info("Unsubscribed {} tokens from topic: {}", tokens.size(), topic);
    }

    private String sendAndGetResponse(Message message) throws InterruptedException, ExecutionException {
        return FirebaseMessaging.getInstance().sendAsync(message).get();
    }

    private AndroidConfig getAndroidConfig(String topic) {
        return AndroidConfig.builder()
                .setTtl(Duration.ofMinutes(2).toMillis())
                .setCollapseKey(topic)
                .setPriority(AndroidConfig.Priority.HIGH)
                .setNotification(AndroidNotification.builder().setTag(topic).build())
                .build();
    }

    private ApnsConfig getApnsConfig(String topic) {
        return ApnsConfig.builder()
                .setAps(Aps.builder().setCategory(topic).setThreadId(topic).build())
                .build();
    }

    private Message buildMessage(NotificationRequest request) {
        Message.Builder builder = getPreconfiguredMessageBuilder(request);

        if (request.getToken() != null && !request.getToken().isBlank()) {
            builder.setToken(request.getToken());
        } else if (request.getTopic() != null && !request.getTopic().isBlank()) {
            builder.setTopic(request.getTopic());
        } else {
            throw new IllegalArgumentException("Phải có token hoặc topic");
        }

        // Thêm data nếu có
        if (request.getData() != null && !request.getData().isEmpty()) {
            builder.putAllData(request.getData());
        }

        return builder.build();
    }

    private Message buildMessageForTopic(NotificationRequest request) {
        if (request.getTopic() == null || request.getTopic().isBlank()) {
            throw new IllegalArgumentException("Topic không được để trống");
        }

        Message.Builder builder = Message.builder()
                .setTopic(request.getTopic())
                .setNotification(Notification.builder()
                        .setTitle(request.getTitle())
                        .setBody(request.getMessage())
                        .build());

        // Thêm config cho Android và iOS
        builder.setAndroidConfig(getAndroidConfig(request.getTopic()));
        builder.setApnsConfig(getApnsConfig(request.getTopic()));

        // Thêm data nếu có
        if (request.getData() != null && !request.getData().isEmpty()) {
            builder.putAllData(request.getData());
        }

        return builder.build();
    }

    private Message.Builder getPreconfiguredMessageBuilder(NotificationRequest request) {
        String topic = request.getTopic() != null ? request.getTopic() : "default";
        AndroidConfig androidConfig = getAndroidConfig(topic);
        ApnsConfig apnsConfig = getApnsConfig(topic);

        return Message.builder()
                .setApnsConfig(apnsConfig)
                .setAndroidConfig(androidConfig)
                .setNotification(Notification.builder()
                        .setTitle(request.getTitle())
                        .setBody(request.getMessage())
                        .build());
    }
}
