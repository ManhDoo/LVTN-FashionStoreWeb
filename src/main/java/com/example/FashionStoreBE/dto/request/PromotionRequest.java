package com.example.FashionStoreBE.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PromotionRequest {
    private String tenKhuyenMai;
    private double giaTriGiam;
    private String hinhThucGiam;
    private String loaiKhuyenMai;
    private String moTa;
    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayKetThuc;
    private String trangThai;
}
