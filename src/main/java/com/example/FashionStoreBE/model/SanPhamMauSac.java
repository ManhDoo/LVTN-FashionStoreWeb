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
@Table(name = "san_pham_mau_sac")
public class SanPhamMauSac {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private SanPham sanPham;

    @ManyToOne
    private MauSac mauSac;

    // ✅ Đây là nơi lưu ảnh theo màu
    @Convert(converter = ImageListConverter.class)
    @Column(length = 512)
    private List<String> hinhAnh;
}
