package com.example.FashionStoreBE.payment.Momo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;

public class MomoHttpUtil {

    private static final Logger logger = LoggerFactory.getLogger(MomoHttpUtil.class);

    public static String sendPost(String endpoint, Map<String, Object> params) throws Exception {
        URL url = new URL(endpoint);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);

        // Chuyển params thành JSON
        ObjectMapper mapper = new ObjectMapper();
        String jsonInputString = mapper.writeValueAsString(params);
        logger.info("MoMo request JSON: {}", jsonInputString);

        // Gửi dữ liệu
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // Đọc phản hồi
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            logger.error("MoMo API returned error code: {}", responseCode);
            // Đọc chi tiết lỗi từ MoMo
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                logger.error("MoMo API error response: {}", response.toString());
            }
            throw new Exception("MoMo API returned HTTP " + responseCode);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            // Phân tích JSON phản hồi để lấy payUrl
            Map<String, Object> responseMap = mapper.readValue(response.toString(), Map.class);
            return (String) responseMap.get("payUrl");
        }
    }
}
