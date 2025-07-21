package com.example.FashionStoreBE.repository;

import com.example.FashionStoreBE.model.BinhLuan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<BinhLuan, Integer> {
    boolean existsByKhachHang_MaKhachHangAndChiTietDonHang_Id(int khachHangId, int chiTietDonHangId);
    List<BinhLuan> findByChiTietDonHang_ChiTietSanPham_SanPham_MaSanPham(int maSanPham);

}
