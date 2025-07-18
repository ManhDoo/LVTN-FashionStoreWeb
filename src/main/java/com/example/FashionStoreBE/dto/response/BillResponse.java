package com.example.FashionStoreBE.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillResponse {
    private int maHoaDon;
    private int maDonHang;
    private String tenNguoiNhan;
    private int soDienThoai;
    private String diaChi;
    private double phiGiaoHang;
    private double thanhTien;
    private double tongGia;
    private String ghiChu;
    private String trangThai;
    private LocalDateTime ngayTao;
    private List<BillDetailResponse> chiTietDonHang;
}
