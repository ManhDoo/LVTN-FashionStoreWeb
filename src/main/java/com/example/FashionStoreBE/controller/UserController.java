package com.example.FashionStoreBE.controller;

import com.example.FashionStoreBE.config.TokenProvider;
import com.example.FashionStoreBE.dto.request.LoginRequest;
import com.example.FashionStoreBE.dto.request.UpdateProfileRequest;
import com.example.FashionStoreBE.dto.response.LoginResponse;
import com.example.FashionStoreBE.dto.response.ProfileResponse;
import com.example.FashionStoreBE.model.KhachHang;
import com.example.FashionStoreBE.repository.UserRepository;
import com.example.FashionStoreBE.service.OtpService;
import com.example.FashionStoreBE.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.hibernate.sql.ast.SqlTreeCreationLogger.LOGGER;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpService otpService;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    @PostMapping("/register")
    public KhachHang register (@RequestBody KhachHang khachHang){
        return userService.register(khachHang);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return userService.login(request);
    }

    @PostMapping("/login-admin")
    public LoginResponse loginForAdmin(@RequestBody LoginRequest request) {
        return userService.loginForAdmin(request);
    }

    @GetMapping("/profile")
    public ResponseEntity<List<ProfileResponse>> getProfile(@RequestHeader("Authorization") String authorization){
        List<ProfileResponse> profile = userService.getProfileByUserId(authorization);
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(
            @RequestHeader("Authorization") String token,
            @RequestBody UpdateProfileRequest request) {
        userService.updateProfile(token, request);
        return ResponseEntity.ok("Cập nhật thông tin thành công");
    }

    @PostMapping("/google")
    public ResponseEntity<LoginResponse> googleLogin(@RequestBody Map<String, String> payload) throws Exception {
        String code = payload.get("code");
        LOGGER.info("Received Google auth code: " + code);

        // Trao đổi mã code để lấy access token
        GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                "https://oauth2.googleapis.com/token",
                clientId,
                clientSecret,
                code,
                redirectUri
        ).execute();

        String accessToken = tokenResponse.getAccessToken();
        LOGGER.info("Access token: " + accessToken);

        // Lấy thông tin người dùng từ Google
        String userInfoEndpoint = "https://www.googleapis.com/oauth2/v3/userinfo";
        String userInfoResponse = new NetHttpTransport()
                .createRequestFactory()
                .buildGetRequest(new com.google.api.client.http.GenericUrl(userInfoEndpoint))
                .setHeaders(new com.google.api.client.http.HttpHeaders().setAuthorization("Bearer " + accessToken))
                .execute()
                .parseAsString();

        // Phân tích JSON với Jackson
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(userInfoResponse);
        String email = jsonNode.get("email").asText();
        String fullName = jsonNode.get("name") != null ? jsonNode.get("name").asText() : "Unknown";

        LOGGER.info("User info - Email: " + email + ", FullName: " + fullName);

        if (email == null) {
            return ResponseEntity.badRequest().body(new LoginResponse(null, null, null, 0));
        }

        // Tạo hoặc lấy người dùng từ cơ sở dữ liệu
        KhachHang user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    KhachHang newUser = new KhachHang();
                    newUser.setEmail(email);
                    newUser.setHoTen(fullName);
                    newUser.setMatKhau("");
                    newUser.setQuyen("USER");
                    newUser.setNgayTao(LocalDateTime.now());
                    newUser.setNgayCapNhat(LocalDateTime.now());
                    LOGGER.info("Creating new user: " + email);
                    return userRepository.save(newUser);
                });

        String jwt = tokenProvider.generateToken(user);

        return ResponseEntity.ok(new LoginResponse(jwt, user.getEmail(), user.getQuyen(), user.getMaKhachHang()));
    }

    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestParam String email) {
        otpService.sendOtp(email);
        return ResponseEntity.ok("Đã gửi OTP đến email");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String email,
                                                @RequestParam String otp,
                                                @RequestParam String newPassword) {
        if (!otpService.verifyOtp(email, otp)) {
            return ResponseEntity.badRequest().body("OTP không hợp lệ hoặc đã hết hạn");
        }

        userService.resetPassword(email, newPassword);
        return ResponseEntity.ok("Đặt lại mật khẩu thành công");
    }

}
