package com.example.FashionStoreBE.dto.request;

import com.example.FashionStoreBE.model.GioHang;
import lombok.Data;

import java.util.List;

@Data
public class GuestOrderRequest {
    private Integer maKhachHang;
    private String tenNguoiNhan;
    private int soDienThoaiNguoiNhan;
    private String emailNguoiNhan;
    private String duong;
    private String xa;
    private String huyen;
    private String tinh;
    private List<GioHang> cart;
    private int pttt;
}
