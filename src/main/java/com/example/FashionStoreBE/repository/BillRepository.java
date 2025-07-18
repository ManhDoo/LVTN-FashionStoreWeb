package com.example.FashionStoreBE.repository;

import com.example.FashionStoreBE.model.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillRepository extends JpaRepository<HoaDon, Integer> {
    boolean existsByDonDatHang_MaDonHang(int maDonHang);
    void deleteByDonDatHang_MaDonHang(int maDonHang);
}
