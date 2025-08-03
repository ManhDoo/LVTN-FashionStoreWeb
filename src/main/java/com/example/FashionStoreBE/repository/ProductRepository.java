package com.example.FashionStoreBE.repository;

import com.example.FashionStoreBE.model.DanhGia;
import com.example.FashionStoreBE.model.KhuyenMai;
import com.example.FashionStoreBE.model.SanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductRepository extends JpaRepository<SanPham, Integer> {
    List<SanPham> findByDanhMucPhai(String phai);

    @Query("SELECT p FROM SanPham p WHERE LOWER(p.tensp) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<SanPham> searchByKeyword(@Param("keyword") String keyword);

    List<SanPham> findByDanhMuc_MaDanhMuc(int maDanhMuc);

    Page<SanPham> findAll(Pageable pageable);

    List<SanPham> findByKhuyenMaiIsNotNull();

    List<SanPham> findByKhuyenMai(KhuyenMai khuyenMai);

    long countByDanhMuc_MaDanhMuc(int maDanhMuc);

    Page<SanPham> findByNgayTaoAfter(LocalDateTime sevenDaysAgo, Pageable pageable);

    // ProductRepository.java
    @Query("""
    SELECT sp 
    FROM SanPham sp 
    JOIN ChiTietSanPham ctsp ON ctsp.sanPham = sp
    JOIN ChiTietDonHang ctdh ON ctdh.chiTietSanPham = ctsp
    GROUP BY sp
    ORDER BY SUM(ctdh.soLuong) DESC
    """)
    Page<SanPham> findTopSellingProducts(Pageable pageable);

    Page<SanPham> findByIsDeletedTrue(Pageable pageable);

    Page<SanPham> findByIsVisibleTrue(Pageable pageable);
}
