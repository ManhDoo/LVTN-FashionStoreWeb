package com.example.FashionStoreBE.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "don_dat_hang")
public class DonHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int maDonHang;
    private int tongSoLuong;
    private double phiGiaoHang;
    private double tongGia;
    private String tenNguoiNhan;
    private int soDienThoaiNguoiNhan;
    private String emailNguoiNhan;
    private String duong;
    private String xa;
    private String huyen;
    private String tinh;
    private LocalDateTime ngayTao;
    private LocalDateTime ngayCapNhat;
    private String trangThai;

    @ManyToOne
    private KhachHang khachHang;

    @ManyToOne
    private PhuongThucThanhToan phuongThucThanhToan;

    @OneToMany(mappedBy = "donDatHang", cascade = CascadeType.ALL)
    private List<ChiTietDonHang> chiTietDonHangs;

    private boolean coYeuCauDoiTra;

}
