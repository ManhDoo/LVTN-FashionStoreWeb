package com.example.FashionStoreBE.controller;

import com.example.FashionStoreBE.service.FirebaseMessagingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class FirebaseController {
    @Autowired
    private FirebaseMessagingService firebaseMessagingService;

    @PostMapping("/subscribe-to-topic")
    public Map<String, String> subscribeToTopic(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String topic = request.get("topic");
        try {
            com.google.firebase.messaging.FirebaseMessaging.getInstance()
                    .subscribeToTopic(List.of(token), topic);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Đăng ký topic thành công!");
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi đăng ký topic: " + e.getMessage());
        }
    }

    @PostMapping("/unsubscribe-from-topic")
    public Map<String, String> unsubscribeFromTopic(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String topic = request.get("topic");
        try {
            com.google.firebase.messaging.FirebaseMessaging.getInstance()
                    .unsubscribeFromTopic(List.of(token), topic);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Hủy đăng ký topic thành công!");
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi hủy đăng ký topic: " + e.getMessage());
        }
    }
}
