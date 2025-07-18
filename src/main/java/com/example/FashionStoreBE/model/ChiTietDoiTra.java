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
@Table(name = "chi_tiet_doi_tra")
public class ChiTietDoiTra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private PhieuDoiTra phieuDoiTra;

    @ManyToOne
    private ChiTietDonHang chiTietDonHang;

    @Convert(converter = ImageListConverter.class)
    @Column(length = 512)
    private List<String> hinhAnhMinhChung;
    private int soLuongDoi;
    private String lyDoChiTiet;
}
