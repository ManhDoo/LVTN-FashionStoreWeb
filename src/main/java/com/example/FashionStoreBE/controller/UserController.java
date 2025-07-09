package com.example.FashionStoreBE.controller;

import com.example.FashionStoreBE.config.TokenProvider;
import com.example.FashionStoreBE.dto.request.LoginRequest;
import com.example.FashionStoreBE.dto.response.LoginResponse;
import com.example.FashionStoreBE.dto.response.ProfileResponse;
import com.example.FashionStoreBE.model.KhachHang;
import com.example.FashionStoreBE.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenProvider tokenProvider;

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
}
