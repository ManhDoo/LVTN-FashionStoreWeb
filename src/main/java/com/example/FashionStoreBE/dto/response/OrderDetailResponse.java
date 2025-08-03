package com.example.FashionStoreBE.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailResponse {
    private int maDonHang;
    private String tenNguoiNhan;
    private String diaChi;
    private String soDienThoai;
    private int id;

    private int maSanPham;
    private String tenSanPham;
    private String hinhAnh;
    private String kichCo;
    private String mauSac;
    private double donGia;
    private double tongGia;
    private int soLuong;
    private LocalDateTime ngayTao;
    private LocalDateTime ngayGiao;
    private double phiGiaoHang;
    private String trangThai;

    private boolean coThanhToan;
    private boolean coYeuCauDoiTra;
}
