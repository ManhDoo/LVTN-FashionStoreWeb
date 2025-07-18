package com.example.FashionStoreBE.repository;

import com.example.FashionStoreBE.model.PhieuDoiTra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PhieuDoiTraRepository extends JpaRepository<PhieuDoiTra, Integer> {
    @Query("SELECT p FROM PhieuDoiTra p WHERE p.donHang.khachHang.maKhachHang = :userId")
    List<PhieuDoiTra> findAllByUserId(@Param("userId") int userId);
}
