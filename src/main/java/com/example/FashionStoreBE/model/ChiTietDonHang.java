package com.example.FashionStoreBE.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "chi_tiet_don_hang")
public class ChiTietDonHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private DonHang donDatHang;

    @ManyToOne
    private ChiTietSanPham chiTietSanPham;

    private int soLuong;
    private double donGia;
    private double soTienGiamGia;

    @OneToMany(mappedBy = "chiTietDonHang", cascade = CascadeType.ALL)
    private List<DanhGia> danhGias;

    @OneToMany(mappedBy = "chiTietDonHang", cascade = CascadeType.ALL)
    private List<BinhLuan> binhLuans;

}
