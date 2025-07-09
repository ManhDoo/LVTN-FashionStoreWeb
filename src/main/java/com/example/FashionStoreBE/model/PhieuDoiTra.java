package com.example.FashionStoreBE.model;

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
@Table(name = "phieu_doi_tra")
public class PhieuDoiTra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int maPhieu;

    @ManyToOne
    private DonHang donHang;

    private LocalDateTime ngayTao;
    private String lyDo;
    private String loai; // "doi" hoặc "tra"
    private String trangThai; // VD: "cho xac nhan", "da xac nhan", "da huy"

    @OneToMany(mappedBy = "phieuDoiTra", cascade = CascadeType.ALL)
    private List<ChiTietDoiTra> chiTietDoiTras;
}
