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
@Table(name = "khach_hang")
public class KhachHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int maKhachHang;
    private String hoTen;
    private String email;
    private String matKhau;
    private int soDienThoai;
    private String duong;
    private String xa;
    private String huyen;
    private String tinh;
    private String quyen;
    private LocalDateTime ngayTao;
    private LocalDateTime ngayCapNhat;
}
