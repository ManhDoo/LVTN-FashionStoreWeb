package com.example.FashionStoreBE.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@Configuration
public class FirebaseConfig {

    private static final String FIREBASE_CONFIG_PATH = "/etc/secrets/firebase.json"; // đường dẫn file Render mount vào

    @PostConstruct
    public void initialize() {
        try {
            FileInputStream serviceAccount = new FileInputStream(FIREBASE_CONFIG_PATH);

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("✅ Firebase đã được khởi tạo từ file secret.");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("❌ Không tìm thấy file Firebase tại: " + FIREBASE_CONFIG_PATH);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("❌ Không thể khởi tạo Firebase: " + e.getMessage());
        }
    }
}
