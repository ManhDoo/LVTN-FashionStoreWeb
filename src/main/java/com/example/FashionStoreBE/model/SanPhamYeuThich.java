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
@Table(name = "san_pham_yeu_thich", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"khach_hang_id", "san_pham_id"})
})
public class SanPhamYeuThich {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "khach_hang_id")
    private KhachHang khachHang;

    @ManyToOne
    @JoinColumn(name = "san_pham_id")
    private SanPham sanPham;

    private LocalDateTime ngayThem;
}
