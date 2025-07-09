package com.example.FashionStoreBE.repository;

import com.example.FashionStoreBE.model.SanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<SanPham, Integer> {
    List<SanPham> findByDanhMucPhai(String phai);

    @Query("SELECT p FROM SanPham p WHERE LOWER(p.tensp) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<SanPham> searchByKeyword(@Param("keyword") String keyword);

    List<SanPham> findByDanhMuc_MaDanhMuc(int maDanhMuc);

    Page<SanPham> findAll(Pageable pageable);

    List<SanPham> findByKhuyenMaiIsNotNull();



}
