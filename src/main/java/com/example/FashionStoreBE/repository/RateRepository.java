package com.example.FashionStoreBE.repository;

import com.example.FashionStoreBE.model.DanhGia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RateRepository extends JpaRepository<DanhGia, Integer> {
    boolean existsByKhachHang_MaKhachHangAndChiTietDonHang_Id(int khachHangId, int chiTietDonHangId);
    List<DanhGia> findByChiTietDonHang_ChiTietSanPham_SanPham_MaSanPham(int maSanPham);

}
