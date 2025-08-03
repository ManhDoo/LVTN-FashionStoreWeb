package com.example.FashionStoreBE.service;

import com.example.FashionStoreBE.model.PasswordResetToken;
import com.example.FashionStoreBE.repository.PasswordResetTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@AllArgsConstructor
public class OtpService {

    private final PasswordResetTokenRepository tokenRepository;

    private final EmailService emailService;

    private final int EXPIRATION_MINUTES = 5;

    @Transactional
    public void sendOtp(String email) {
        String otp = generateOtp();

        // Xoá OTP cũ
        tokenRepository.deleteByEmail(email);

        // Lưu OTP mới
        PasswordResetToken token = new PasswordResetToken();
        token.setEmail(email);
        token.setOtp(otp);
        token.setExpiredAt(LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES));
        tokenRepository.save(token);

        String subject = "Mã OTP đặt lại mật khẩu";
        String body = "<h3>Mã OTP của bạn là: <b>" + otp + "</b></h3><p>OTP có hiệu lực trong 5 phút.</p>";
        emailService.sendOrderEmail(email, subject, body);
    }

    public boolean verifyOtp(String email, String otp) {
        return tokenRepository.findByEmailAndOtp(email, otp)
                .filter(t -> t.getExpiredAt().isAfter(LocalDateTime.now()))
                .isPresent();
    }

    private String generateOtp() {
        return String.format("%06d", new Random().nextInt(999999));
    }
}
