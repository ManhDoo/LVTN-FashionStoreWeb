package com.example.FashionStoreBE.service;

import com.example.FashionStoreBE.config.VNPayConfig;
import com.example.FashionStoreBE.model.DonHang;
import com.example.FashionStoreBE.repository.*;
import com.example.FashionStoreBE.service.impl.OrderServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Service
@AllArgsConstructor
public class VNPayService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    private final OrderRepository donHangRepo;
    private final EmailService emailService;

    public String createVNPayPaymentUrl(DonHang donHang, String orderInfo) throws Exception {
        String vnp_Version = VNPayConfig.VNP_VERSION;
        String vnp_Command = VNPayConfig.VNP_COMMAND;
        String vnp_OrderType = VNPayConfig.VNP_ORDER_TYPE;
        String vnp_TmnCode = VNPayConfig.VNP_TMN_CODE;
        String vnp_ReturnUrl = VNPayConfig.VNP_RETURN_URL;
        String vnp_IpAddr = "127.0.0.1"; // Có thể lấy từ request nếu cần
        String vnp_TxnRef = donHang.getMaDonHang() + "_" + System.currentTimeMillis();
        long amount = (long) (donHang.getTongGia() * 100); // VNPay yêu cầu số tiền * 100

        Map<String, String> vnp_Params = new TreeMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_OrderType", vnp_OrderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        vnp_Params.put("vnp_CreateDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

        StringBuilder query = new StringBuilder();
        for (Map.Entry<String, String> entry : vnp_Params.entrySet()) {
            query.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString()))
                    .append("=")
                    .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()))
                    .append("&");
        }
        String queryString = query.toString();
        queryString = queryString.substring(0, queryString.length() - 1);

        String vnp_SecureHash = hmacSHA512(VNPayConfig.VNP_HASH_SECRET, queryString);
        queryString += "&vnp_SecureHash=" + vnp_SecureHash;

        return VNPayConfig.VNP_URL + "?" + queryString;
    }

    private String hmacSHA512(String key, String data) throws Exception {
        Mac sha512_HMAC = Mac.getInstance("HmacSHA512");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        sha512_HMAC.init(secret_key);
        byte[] hash = sha512_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    @Transactional
    public void handleVNPayReturn(HttpServletRequest request, HttpServletResponse response) throws IOException, java.io.IOException {
        Map<String, String> vnp_Params = new HashMap<>();
        for (String key : request.getParameterMap().keySet()) {
            vnp_Params.put(key, request.getParameter(key));
        }

        String vnp_SecureHash = vnp_Params.get("vnp_SecureHash");
        vnp_Params.remove("vnp_SecureHash");

        StringBuilder data = new StringBuilder();
        for (Map.Entry<String, String> entry : new TreeMap<>(vnp_Params).entrySet()) {
            data.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
                    .append("=")
                    .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                    .append("&");
        }
        String signData = data.substring(0, data.length() - 1);
        String computedHash;
        try {
            computedHash = hmacSHA512(VNPayConfig.VNP_HASH_SECRET, signData);
        } catch (Exception e) {
            logger.error("Error computing HMAC SHA512: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Lỗi khi xác thực chữ ký VNPay");
            return;
        }

        if (!computedHash.equalsIgnoreCase(vnp_SecureHash)) {
            logger.error("Invalid VNPay signature");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Chữ ký VNPay không hợp lệ");
            return;
        }

        String vnp_TxnRef = vnp_Params.get("vnp_TxnRef");
        String orderId = vnp_TxnRef.split("_")[0];
        String vnp_ResponseCode = vnp_Params.get("vnp_ResponseCode");

        DonHang donHang = donHangRepo.findById(Integer.parseInt(orderId))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));

        if ("00".equals(vnp_ResponseCode)) {
            donHang.setCoThanhToan(true);
            donHang.setNgayCapNhat(LocalDateTime.now());
            donHangRepo.save(donHang);
            logger.info("Payment successful for order ID: {}", orderId);

            String email = donHang.getEmailNguoiNhan() != null ? donHang.getEmailNguoiNhan() : donHang.getKhachHang().getEmail();
            String subject = "Thanh toán thành công cho đơn hàng #" + donHang.getMaDonHang();
            String body = "Chào " + donHang.getTenNguoiNhan() + ",\n\n"
                    + "Thanh toán cho đơn hàng #" + donHang.getMaDonHang() + " đã thành công.\n"
                    + "Tổng tiền: " + donHang.getTongGia() + " VND\n"
                    + "Trạng thái: " + donHang.getTrangThai() + "\n\n"
                    + "Cảm ơn bạn đã mua sắm tại FashionStore!";
            emailService.sendOrderEmail(email, subject, body);

            // Chuyển hướng đến trang localhost
            String redirectUrl = "http://localhost:5173/vnpay-return?status=success&orderId=" + orderId;
            response.sendRedirect(redirectUrl);
        } else {
            donHang.setCoThanhToan(false);
            donHang.setNgayCapNhat(LocalDateTime.now());
            donHangRepo.save(donHang);
            String redirectUrl = "http://localhost:5173/vnpay-return?status=failure&orderId=" + orderId;
            response.sendRedirect(redirectUrl);
        }
    }
}
