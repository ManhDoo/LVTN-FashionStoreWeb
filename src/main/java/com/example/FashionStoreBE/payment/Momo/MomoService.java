package com.example.FashionStoreBE.payment.Momo;

import com.example.FashionStoreBE.exception.ApiException;
import com.example.FashionStoreBE.exception.ResourceNotFoundException;
import com.example.FashionStoreBE.model.DonHang;
import com.example.FashionStoreBE.repository.OrderRepository;
import com.example.FashionStoreBE.service.EmailService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDateTime;
import java.util.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class MomoService {

    private static final Logger logger = LoggerFactory.getLogger(MomoService.class);

    private final String partnerCode;
    private final String accessKey;
    private final String secretKey;
    private final String returnUrl;
    private final String notifyUrl;
    private final OrderRepository orderRepository;
    private final EmailService emailService;

    // Constructor injection
    public MomoService(
            @Value("${momo.partnerCode}") String partnerCode,
            @Value("${momo.accessKey}") String accessKey,
            @Value("${momo.secretKey}") String secretKey,
            @Value("${momo.returnUrl}") String returnUrl,
            @Value("${momo.notifyUrl}") String notifyUrl,
            OrderRepository donHangRepo,
            EmailService emailService
    ) {
        this.partnerCode = partnerCode;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.returnUrl = returnUrl;
        this.notifyUrl = notifyUrl;
        this.orderRepository = donHangRepo;
        this.emailService = emailService;
    }

    public String createPaymentUrl(DonHang order, String orderInfo) throws Exception {
        if (!"CHO_XAC_NHAN".equals(order.getTrangThai())) {
            throw new ApiException("Đơn hàng không ở trạng thái CHO_XAC_NHAN");
        }

        String endpoint = "https://test-payment.momo.vn/v2/gateway/api/create";
        String requestId = UUID.randomUUID().toString();
        String orderId = "ORDER" + order.getMaDonHang();

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("partnerCode", partnerCode);
        params.put("accessKey", accessKey);
        params.put("requestId", requestId);
        params.put("amount", String.valueOf((int)(order.getTongGia() + order.getPhiGiaoHang())));
        params.put("orderId", orderId);
        params.put("orderInfo", orderInfo);
        params.put("redirectUrl", returnUrl); // Sửa từ returnUrl thành redirectUrl theo tài liệu MoMo
        params.put("ipnUrl", notifyUrl); // Sửa từ notifyUrl thành ipnUrl
        params.put("extraData", "");
        params.put("requestType", "captureWallet");

        // Log dữ liệu để kiểm tra
        logger.info("MoMo request params: {}", params);

        String rawHash = String.format("accessKey=%s&amount=%s&extraData=%s&ipnUrl=%s&orderId=%s&orderInfo=%s&partnerCode=%s&redirectUrl=%s&requestId=%s&requestType=%s",
                accessKey,
                params.get("amount"),
                params.get("extraData"),
                notifyUrl,
                orderId,
                orderInfo,
                partnerCode,
                returnUrl,
                requestId,
                params.get("requestType")
        );

        // Log rawHash để kiểm tra
        logger.info("MoMo rawHash: {}", rawHash);

        String signature = hmacSHA256(rawHash, secretKey);
        params.put("signature", signature);

        // Log signature
        logger.info("MoMo signature: {}", signature);

        try {
            String payUrl = MomoHttpUtil.sendPost(endpoint, params);
            if (payUrl == null || payUrl.isEmpty()) {
                throw new ApiException("Không thể tạo URL thanh toán từ MoMo");
            }
            logger.info("MoMo payment URL: {}", payUrl);
            return payUrl;
        } catch (Exception e) {
            logger.error("Failed to create MoMo payment URL for order {}: {}", order.getMaDonHang(), e.getMessage());
            throw new ApiException("Lỗi khi tạo URL thanh toán: " + e.getMessage());
        }
    }

    @Transactional
    public String handleNotify(Map<String, Object> data) throws Exception {
        // Kiểm tra chữ ký
        String receivedSignature = (String) data.get("signature");
        String rawHash = String.format("accessKey=%s&amount=%s&extraData=%s&message=%s&orderId=%s&orderInfo=%s&orderType=%s&partnerCode=%s&payType=%s&requestId=%s&responseTime=%s&resultCode=%s&transId=%s",
                accessKey,
                data.get("amount"),
                data.get("extraData"),
                data.get("message"),
                data.get("orderId"),
                data.get("orderInfo"),
                data.get("orderType"),
                partnerCode,
                data.get("payType"),
                data.get("requestId"),
                data.get("responseTime"),
                data.get("resultCode"),
                data.get("transId")
        );
        String computedSignature = hmacSHA256(rawHash, secretKey);

        if (!computedSignature.equals(receivedSignature)) {
            throw new ApiException("Invalid signature");
        }

        // Kiểm tra resultCode
        int resultCode = Integer.parseInt(data.get("resultCode").toString());

        String orderId = (String) data.get("orderId");
        int maDonHang;
        try {
            maDonHang = Integer.parseInt(orderId.replace("ORDER", ""));
        } catch (NumberFormatException e) {
            logger.error("Invalid orderId format: {}", orderId);
            throw new ApiException("Định dạng orderId không hợp lệ");
        }

        DonHang donHang = orderRepository.findById(maDonHang)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng: " + maDonHang));

        if (resultCode == 0) { // Thanh toán thành công
            donHang.setTrangThai("DA_THANH_TOAN");
            donHang.setCoThanhToan(true);
            donHang.setNgayCapNhat(LocalDateTime.now());
            orderRepository.save(donHang);

            // Gửi email xác nhận thanh toán
            String email = donHang.getEmailNguoiNhan();
            if (email != null && !email.isEmpty()) {
                String subject = "Thanh toán đơn hàng #" + donHang.getMaDonHang() + " thành công";
                String body = "Chào " + donHang.getTenNguoiNhan() + ",\n\n" +
                        "Đơn hàng #" + donHang.getMaDonHang() + " đã được thanh toán thành công qua MoMo.\n" +
                        "Tổng tiền: " + donHang.getTongGia() + " VND\n" +
                        "Cảm ơn bạn đã mua sắm tại FashionStore!";
                emailService.sendOrderEmail(email, subject, body);
            }
        } else {
            donHang.setTrangThai("THANH_TOAN_THAT_BAI");
            orderRepository.save(donHang);
        }

        return "Success";
    }

    public String handleReturn(Map<String, String> params) throws Exception {
        // Kiểm tra chữ ký
        String receivedSignature = params.get("signature");
        String rawHash = String.format("accessKey=%s&amount=%s&extraData=%s&message=%s&orderId=%s&orderInfo=%s&orderType=%s&partnerCode=%s&payType=%s&requestId=%s&responseTime=%s&resultCode=%s&transId=%s",
                accessKey,
                params.get("amount"),
                params.get("extraData"),
                params.get("message"),
                params.get("orderId"),
                params.get("orderInfo"),
                params.get("orderType"),
                partnerCode,
                params.get("payType"),
                params.get("requestId"),
                params.get("responseTime"),
                params.get("resultCode"),
                params.get("transId")
        );
        String computedSignature = hmacSHA256(rawHash, secretKey);

        if (!computedSignature.equals(receivedSignature)) {
            throw new ApiException("Invalid signature");
        }

        String orderId = params.get("orderId");
        int maDonHang;
        try {
            maDonHang = Integer.parseInt(orderId.replace("ORDER", ""));
        } catch (NumberFormatException e) {
            logger.error("Invalid orderId format: {}", orderId);
            throw new ApiException("Định dạng orderId không hợp lệ");
        }

        DonHang donHang = orderRepository.findById(maDonHang)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng: " + maDonHang));

        int resultCode = Integer.parseInt(params.get("resultCode"));
        String redirectUrl;
        if (resultCode == 0) {
            // Thanh toán thành công
            redirectUrl = "https://lvtn-fashion-store-web-fe-9qyp.vercel.app/payment/success?orderId=" + orderId;
        } else {
            // Thanh toán thất bại
            redirectUrl = "https://lvtn-fashion-store-web-fe-9qyp.vercel.app/payment/failed?orderId=" + orderId + "&error=" + URLEncoder.encode(params.get("message"), StandardCharsets.UTF_8.toString());
        }

        return redirectUrl;
    }

    private String hmacSHA256(String data, String key) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secretKey);
        byte[] hash = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
