package com.example.FashionStoreBE.repository;

import com.example.FashionStoreBE.model.ChiTietSanPham;
import com.example.FashionStoreBE.model.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductDetailRopository extends JpaRepository<ChiTietSanPham, Integer> {
    List<ChiTietSanPham> findBySanPham_MaSanPham(int maSanPham);

    @Query("SELECT COUNT(c) > 0 FROM ChiTietDonHang c WHERE c.chiTietSanPham.id = :chiTietId")
    boolean existsInChiTietDonHangByChiTietSanPham(@Param("chiTietId") int chiTietId);

    List<ChiTietSanPham> findBySanPham(SanPham sanPham);

}
