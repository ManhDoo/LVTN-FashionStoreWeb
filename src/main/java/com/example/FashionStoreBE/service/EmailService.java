package com.example.FashionStoreBE.service;

import com.example.FashionStoreBE.model.SanPham;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.List;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired private SpringTemplateEngine templateEngine;

    @Async
    public void sendOrderEmail(String to, String subject, String body) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("dodanhmanha21819@gmail.com"); // email gửi
            helper.setTo(to);                      // email khách
            helper.setSubject(subject);
            helper.setText(body, true); // true để bật HTML
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

    @Async
    public void sendPromotionEmail(String to, List<SanPham> sanPhams) {
        Context context = new Context();
        context.setVariable("sanPhams", sanPhams);
        String htmlContent = templateEngine.process("promotion-email-template.html", context);

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("dodanhmanha21819@gmail.com");
            helper.setTo(to);
            helper.setSubject("🔥 Cập nhật sản phẩm khuyến mãi mới từ FashionStore!");
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Gửi email thất bại: " + e.getMessage());
        }
    }
}