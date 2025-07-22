package com.example.FashionStoreBE.service;

import com.example.FashionStoreBE.dto.response.ThongKe.TopSanPhamDTO;
import com.example.FashionStoreBE.dto.response.ThongKe.TopSanPhamThangDTO;

import java.util.List;
import java.util.Map;

public interface ThongKeService {
    List<Map<String, Object>> thongKeTheoThang(int year);
    List<TopSanPhamThangDTO> getTop3SanPham(String phai, int year);
}
