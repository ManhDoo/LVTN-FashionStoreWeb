package com.example.FashionStoreBE.service;

import com.example.FashionStoreBE.dto.request.LoginRequest;
import com.example.FashionStoreBE.dto.response.LoginResponse;
import com.example.FashionStoreBE.dto.response.ProfileResponse;
import com.example.FashionStoreBE.model.KhachHang;

import java.util.List;

public interface UserService {

    KhachHang register(KhachHang khachHang);

    LoginResponse login(LoginRequest request);

    List<ProfileResponse> getProfileByUserId(String token);

    LoginResponse loginForAdmin(LoginRequest request);

}
