package com.example.FashionStoreBE.repository;

import com.example.FashionStoreBE.dto.response.ThongKe.DoanhThuThangDTO;
import com.example.FashionStoreBE.model.DonHang;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<DonHang, Integer> {
    List<DonHang> findByKhachHang_MaKhachHang(int maKhachHang);

    Page<DonHang> findByTrangThai(String trangThai, Pageable pageable);

    Page<DonHang> findByKhachHang_MaKhachHang(int maKhachHang, Pageable pageable);

    @Query(value = """
        SELECT 
            MONTH(d.ngay_tao) AS thang,
            SUM(d.tong_gia) AS doanhThu,
            COUNT(d.ma_don_hang) AS soDon,
            SUM(d.tong_so_luong) AS soLuongSanPham
        FROM don_dat_hang d
        WHERE YEAR(d.ngay_tao) = :year AND d.trang_thai = 'DA_GIAO'
        GROUP BY MONTH(d.ngay_tao)
        ORDER BY thang
    """, nativeQuery = true)
    List<DoanhThuThangDTO> thongKeDoanhThuTheoThang(int year);

}
