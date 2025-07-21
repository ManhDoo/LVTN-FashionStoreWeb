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
@Table(name = "binh_luan")
public class BinhLuan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Người bình luận
    @ManyToOne
    private KhachHang khachHang;

    // Sản phẩm cụ thể trong đơn hàng
    @ManyToOne
    private ChiTietDonHang chiTietDonHang;

    // Nội dung bình luận
    @Column(columnDefinition = "TEXT")
    private String noiDung;

    @Convert(converter = ImageListConverter.class)
    private List<String> hinhAnh;

    private LocalDateTime ngayBinhLuan;
}
