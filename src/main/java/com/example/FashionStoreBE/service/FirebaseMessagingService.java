package com.example.FashionStoreBE.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FirebaseMessagingService {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseMessagingService.class);

    public void sendNotificationToTopic(String topic, String title, String body) {
        try {
            Message message = Message.builder()
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .setTopic(topic)
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            logger.info("Gửi thông báo thành công: {}", response);
        } catch (Exception e) {
            logger.error("Lỗi khi gửi thông báo: {}", e.getMessage());
            throw new RuntimeException("Không thể gửi thông báo: " + e.getMessage());
        }
    }
}