package com.example.FashionStoreBE.service.impl;

import com.example.FashionStoreBE.config.TokenProvider;
import com.example.FashionStoreBE.dto.request.LoginRequest;
import com.example.FashionStoreBE.dto.response.LoginResponse;
import com.example.FashionStoreBE.dto.response.ProfileResponse;
import com.example.FashionStoreBE.exception.UserException;
import com.example.FashionStoreBE.model.KhachHang;
import com.example.FashionStoreBE.repository.UserRepository;
import com.example.FashionStoreBE.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenProvider tokenProvider;

    @Override
    public KhachHang register(KhachHang khachHang) {

        if (userRepository.findByEmail(khachHang.getEmail()).isPresent())
            throw new UserException("Email đã tồn tại");

        khachHang.setMatKhau(passwordEncoder.encode(khachHang.getMatKhau()));
        khachHang.setNgayTao(LocalDateTime.now());
        khachHang.setNgayCapNhat(LocalDateTime.now());
        khachHang.setQuyen("USER");

        return userRepository.save(khachHang);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        KhachHang user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserException("Email không tồn tại"));

        if (!passwordEncoder.matches(request.getMatKhau(), user.getMatKhau())) {
            throw new UserException("Mật khẩu không đúng");
        }

        String jwt = tokenProvider.generateToken(user);

        return new LoginResponse(jwt, user.getEmail(), user.getQuyen(), user.getMaKhachHang());
    }

    @Override
    public LoginResponse loginForAdmin(LoginRequest request) {
        KhachHang user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserException("Email không tồn tại"));

        if (!passwordEncoder.matches(request.getMatKhau(), user.getMatKhau())) {
            throw new UserException("Mật khẩu không đúng");
        }
        if (!"ADMIN".equalsIgnoreCase(user.getQuyen())){
            throw new UserException("Tài khoản không có quyền truy cập");
        }

        String jwt = tokenProvider.generateToken(user);

        return new LoginResponse(jwt, user.getEmail(), user.getQuyen(), user.getMaKhachHang());
    }

    @Override
    public List<ProfileResponse> getProfileByUserId(String token) {
        int userId = tokenProvider.getUserIdFromToken(token);
        return userRepository.findById(userId)
                .stream()
                .map(user -> new ProfileResponse(
                    user.getMaKhachHang(),
                        user.getHoTen(),
                        user.getEmail(),
                        user.getSoDienThoai(),
                        user.getDuong(),
                        user.getXa(),
                        user.getHuyen(),
                        user.getTinh()
                )).collect(Collectors.toList());
    }
}
