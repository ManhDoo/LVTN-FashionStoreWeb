package com.example.FashionStoreBE.service;

import com.example.FashionStoreBE.dto.request.PromotionRequest;
import com.example.FashionStoreBE.model.KhuyenMai;
import com.example.FashionStoreBE.model.SanPham;

import java.util.List;

public interface PromotionService {
    KhuyenMai createPromotion(PromotionRequest request);
    SanPham ganSanPhamVaoKhuyenMai(int maSanPham, int maKhuyenMai);
    List<SanPham> getAllProductPromotion();
}
