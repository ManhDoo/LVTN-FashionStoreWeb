package com.example.FashionStoreBE.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "chi_tiet_doi_tra")
public class ChiTietDoiTra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private PhieuDoiTra phieuDoiTra;

    @ManyToOne
    private ChiTietDonHang chiTietDonHang;

    private int soLuongDoi;
    private String lyDoChiTiet;
}
