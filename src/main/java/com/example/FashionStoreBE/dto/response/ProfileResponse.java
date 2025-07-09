package com.example.FashionStoreBE.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponse {
    private int maKhachHang;
    private String hoTen;
    private String email;
    private int soDienThoai;
    private String duong;
    private String xa;
    private String huyen;
    private String tinh;
}
