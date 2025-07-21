package com.example.FashionStoreBE.repository;

import com.example.FashionStoreBE.model.KhachHang;
import com.example.FashionStoreBE.model.SanPham;
import com.example.FashionStoreBE.model.SanPhamYeuThich;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteItemsRepository extends JpaRepository<SanPhamYeuThich, Integer> {
    Optional<SanPhamYeuThich> findByKhachHangAndSanPham(KhachHang khachHang, SanPham sanPham);
    List<SanPhamYeuThich> findAllByKhachHang(KhachHang khachHang);
    Page<SanPhamYeuThich> findAllByKhachHang(KhachHang khachHang, Pageable pageable);

}
