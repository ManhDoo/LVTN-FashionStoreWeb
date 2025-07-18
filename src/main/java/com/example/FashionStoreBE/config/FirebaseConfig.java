package com.example.FashionStoreBE.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import io.jsonwebtoken.io.IOException;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {
    @PostConstruct
    public void initialize() {
        try {
            // Sử dụng ClassLoader để tải tệp từ resources
            InputStream serviceAccount = getClass().getClassLoader()
                    .getResourceAsStream("fashionstore-e91ec-firebase-adminsdk-fbsvc-81ccafa3fe.json");

            if (serviceAccount == null) {
                throw new FileNotFoundException("Tệp fashionstorebe-firebase-adminsdk.json không được tìm thấy trong resources.");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("Firebase đã được khởi tạo thành công.");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Không tìm thấy tệp cấu hình Firebase: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Không thể khởi tạo Firebase: " + e.getMessage());
        }
    }
}
