package com.example.FashionStoreBE.model;

import com.example.FashionStoreBE.converter.ImageListConverter;
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
@Table(name = "danh_gia")
public class DanhGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Người đánh giá
    @ManyToOne
    private KhachHang khachHang;

    // Sản phẩm cụ thể trong đơn hàng
    @ManyToOne
    private ChiTietDonHang chiTietDonHang;

    // Số sao đánh giá (từ 1 đến 5)
    private int soSao;

    @Column(columnDefinition = "TEXT")
    private String noiDung;

    @Convert(converter = ImageListConverter.class)
    private List<String> hinhAnh;

    private LocalDateTime ngayDanhGia;

    @Column(name = "duyet")
    private boolean duyet = false;

}
