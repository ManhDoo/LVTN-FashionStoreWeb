package com.example.FashionStoreBE.model;

import com.example.FashionStoreBE.converter.ImageListConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "chi_tiet_san_pham")
public class ChiTietSanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private SanPham sanPham;

    @ManyToOne
    private KichCo kichCo;

    @ManyToOne
    private MauSac mauSac;

    @Convert(converter = ImageListConverter.class)
    private List<String> hinhAnh;

    private double giaThem;
    private int tonKho;

    @ManyToOne
    private SanPhamMauSac sanPhamMauSac;

}
