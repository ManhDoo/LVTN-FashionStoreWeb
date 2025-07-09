package com.example.FashionStoreBE.model;

import com.example.FashionStoreBE.converter.ImageListConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "danh_gia_san_pham")
public class DanhGiaSanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int maDanhGia;
    private String binhLuan;
    private int soSao;

    @Convert(converter = ImageListConverter.class)
    private List<String> hinhAnh;

    private LocalDateTime ngayTao;

    @ManyToOne
    private SanPham sanPham;

    @ManyToOne
    private KhachHang khachHang;

    @OneToOne
    private ChiTietDonHang chiTietDonHang;
}
