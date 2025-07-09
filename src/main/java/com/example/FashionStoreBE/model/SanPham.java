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
@Table(name = "san_pham")
public class SanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int maSanPham;
    private String tensp;

    @Convert(converter = ImageListConverter.class)
    @Column(length = 512)
    private List<String> hinhAnh;

    private float giaGoc;
    private String moTa;
    private LocalDateTime ngayTao;
    private LocalDateTime ngayCapNhat;

    @ManyToOne
    private DanhMuc danhMuc;

    @ManyToOne
    private KhuyenMai khuyenMai;
}
