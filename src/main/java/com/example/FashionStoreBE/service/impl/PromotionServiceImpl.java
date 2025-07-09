package com.example.FashionStoreBE.service.impl;

import com.example.FashionStoreBE.dto.request.PromotionRequest;
import com.example.FashionStoreBE.model.KhuyenMai;
import com.example.FashionStoreBE.model.SanPham;
import com.example.FashionStoreBE.repository.ProductRepository;
import com.example.FashionStoreBE.repository.PromotionRepository;
import com.example.FashionStoreBE.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final ProductRepository productRepository;

    @Override
    public KhuyenMai createPromotion(PromotionRequest request) {
        KhuyenMai km = new KhuyenMai();
        km.setTenKhuyenMai(request.getTenKhuyenMai());
        km.setGiaTriGiam(request.getGiaTriGiam());
        km.setHinhThucGiam(request.getHinhThucGiam());
        km.setLoaiKhuyenMai(request.getLoaiKhuyenMai());
        km.setMoTa(request.getMoTa());
        km.setNgayBatDau(request.getNgayBatDau());
        km.setNgayKetThuc(request.getNgayKetThuc());
        km.setTrangThai(request.getTrangThai());
        return promotionRepository.save(km);
    }

    @Override
    public SanPham ganSanPhamVaoKhuyenMai(int maSanPham, int maKhuyenMai) {
        SanPham sp = productRepository.findById(maSanPham)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));
        KhuyenMai km = promotionRepository.findById(maKhuyenMai)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mãi"));

        sp.setKhuyenMai(km);
        sp.setNgayCapNhat(LocalDateTime.now());
        return productRepository.save(sp);
    }

    @Override
    public List<SanPham> getAllProductPromotion() {
        return productRepository.findByKhuyenMaiIsNotNull();
    }
}
