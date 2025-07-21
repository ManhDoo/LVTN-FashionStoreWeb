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

    private LocalDateTime ngayDanhGia;
}
