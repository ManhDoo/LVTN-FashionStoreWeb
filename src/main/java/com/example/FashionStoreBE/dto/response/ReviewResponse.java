package com.example.FashionStoreBE.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReviewResponse {
    private int id;
    private String hoTenKhachHang;
    private int soSao;
    private String noiDung;
    private List<String> hinhAnh;
    private LocalDateTime ngayDanhGia;
    private String tenSanPham;
    private String mauSac;
    private List<String> hinhAnhSanPham;
}
