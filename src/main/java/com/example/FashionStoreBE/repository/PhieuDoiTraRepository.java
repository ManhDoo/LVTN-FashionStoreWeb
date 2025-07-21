package com.example.FashionStoreBE.repository;

import com.example.FashionStoreBE.model.PhieuDoiTra;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PhieuDoiTraRepository extends JpaRepository<PhieuDoiTra, Integer> {
    Page<PhieuDoiTra> findAllByDonHang_KhachHang_MaKhachHang(int userId, Pageable pageable);
    Page<PhieuDoiTra> findAll(Pageable pageable);
}
