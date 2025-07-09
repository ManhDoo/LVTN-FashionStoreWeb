package com.example.FashionStoreBE.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "hoa_don")
public class HoaDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int maHoaDon;
    private double phiGiaoHang;
    private double thanhTien;
    private double tongGia;
    private String ghiChu;
    private String trangThai;
    private LocalDateTime ngayTao;

    @ManyToOne
    private DonHang donDatHang;


}
