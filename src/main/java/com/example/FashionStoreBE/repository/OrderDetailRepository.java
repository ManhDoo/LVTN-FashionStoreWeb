package com.example.FashionStoreBE.repository;

import com.example.FashionStoreBE.dto.response.ThongKe.TopSanPhamDTO;
import com.example.FashionStoreBE.model.ChiTietDonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<ChiTietDonHang, Integer> {

    @Query(value = """
    SELECT 
        sp.tensp AS tenSanPham,
        SUM(ctdh.so_luong) AS soLuongMua,
        MONTH(d.ngay_tao) AS thang,
        dm.phai AS gioiTinh
    FROM chi_tiet_don_hang ctdh
    JOIN chi_tiet_san_pham ctsp ON ctdh.chi_tiet_san_pham_id = ctsp.id
    JOIN san_pham sp ON ctsp.san_pham_ma_san_pham = sp.ma_san_pham
    JOIN danh_muc dm ON sp.danh_muc_ma_danh_muc = dm.ma_danh_muc
    JOIN don_dat_hang d ON d.ma_don_hang = ctdh.don_dat_hang_ma_don_hang
    WHERE d.trang_thai = 'DA_GIAO'
      AND YEAR(d.ngay_tao) = :year
      AND (:phai IS NULL OR dm.phai = :phai)
    GROUP BY MONTH(d.ngay_tao), sp.ma_san_pham
    ORDER BY thang, soLuongMua DESC
""", nativeQuery = true)
    List<Object[]> getTopSanPhamTheoThang(String phai, int year);



}
