package com.example.FashionStoreBE.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "dia_chi_giao_hang")
public class DiaChiGiaoHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int maDiaChi;
    private String duong;
    private String xa;
    private String huyen;
    private String tinh;

    @ManyToOne
    private KhachHang khachHang;
}
