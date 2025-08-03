package com.example.FashionStoreBE.service;

import com.example.FashionStoreBE.config.TokenProvider;
import com.example.FashionStoreBE.model.KhachHang;
import com.example.FashionStoreBE.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.logging.Logger;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger LOGGER = Logger.getLogger(OAuth2SuccessHandler.class.getName());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        Optional<KhachHang> optional = userRepository.findByEmail(email);

        KhachHang user = optional.orElseGet(() -> {
            KhachHang khachHang = new KhachHang();
            khachHang.setEmail(email);
            khachHang.setHoTen(name);
            khachHang.setMatKhau(""); // Không có mật khẩu
            khachHang.setQuyen("USER");
            khachHang.setNgayTao(LocalDateTime.now());
            khachHang.setNgayCapNhat(LocalDateTime.now());
            return userRepository.save(khachHang);
        });

        String jwt = tokenProvider.generateToken(user);

        // Redirect kèm token về FE (FE lấy token từ query string để lưu vào localStorage)
        String redirectUrl = UriComponentsBuilder
                .fromUriString("http://localhost:5173/oauth2/redirect")
                .queryParam("token", jwt)
                .build().toUriString();

        response.sendRedirect(redirectUrl);
    }
}