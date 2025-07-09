package com.example.FashionStoreBE.repository;

import com.example.FashionStoreBE.model.DonHang;
import com.example.FashionStoreBE.model.TrangThaiDonHang;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<DonHang, Integer> {
    List<DonHang> findByKhachHang_MaKhachHang(int maKhachHang);

    Page<DonHang> findByTrangThai(String trangThai, Pageable pageable);


}
