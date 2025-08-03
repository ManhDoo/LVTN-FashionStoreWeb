package com.example.FashionStoreBE.service.impl;

import com.example.FashionStoreBE.dto.request.PromotionRequest;
import com.example.FashionStoreBE.exception.ApiException;
import com.example.FashionStoreBE.exception.ResourceNotFoundException;
import com.example.FashionStoreBE.model.KhachHang;
import com.example.FashionStoreBE.model.KhuyenMai;
import com.example.FashionStoreBE.model.SanPham;
import com.example.FashionStoreBE.repository.ProductRepository;
import com.example.FashionStoreBE.repository.PromotionRepository;
import com.example.FashionStoreBE.repository.UserRepository;
import com.example.FashionStoreBE.service.EmailService;
import com.example.FashionStoreBE.service.PromotionService;
import com.google.api.gax.rpc.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final ProductRepository productRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;

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
    @Transactional
    public void xoaKhuyenMai(int maKhuyenMai) {
        KhuyenMai km = promotionRepository.findById(maKhuyenMai)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khuyến mãi có mã: " + maKhuyenMai));

        // Kiểm tra có sản phẩm nào đang dùng khuyến mãi này không
        List<SanPham> sanPhams = productRepository.findByKhuyenMai(km);
        if (!sanPhams.isEmpty()) {
            throw new ApiException("Không thể xóa khuyến mãi vì đang áp dụng cho "
                    + sanPhams.size() + " sản phẩm. Vui lòng gỡ khuyến mãi khỏi các sản phẩm trước.");
        }

        km.setDeleted(true);
        promotionRepository.save(km);
    }

    @Override
    public KhuyenMai suaKhuyenMai(int maKhuyenMai, PromotionRequest request) {
        KhuyenMai km = promotionRepository.findById(maKhuyenMai)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mãi"));

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
        SanPham saved = productRepository.save(sp);

        // Lấy danh sách khách hàng
        List<KhachHang> khachHangs = userRepository.findAll();

        // Gửi email
        for (KhachHang kh : khachHangs) {
            emailService.sendPromotionEmail(kh.getEmail(), List.of(saved));
        }

        return saved;
    }

    @Override
    public SanPham goBoKhuyenMaiKhoiSanPham(int maSanPham) {
        SanPham sp = productRepository.findById(maSanPham)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        if (sp.getKhuyenMai() == null) {
            throw new RuntimeException("Sản phẩm này không có khuyến mãi nào để gỡ.");
        }

        sp.setKhuyenMai(null); // Gỡ khuyến mãi
        sp.setNgayCapNhat(LocalDateTime.now());

        return productRepository.save(sp);
    }


    @Override
    public List<SanPham> getAllProductPromotion() {
        return productRepository.findByKhuyenMaiIsNotNull();
    }

    @Override
    public List<KhuyenMai> getAllPromotions() {
        return promotionRepository.findAll();
    }

    @Scheduled(fixedRate = 3600000) // Mỗi 1 giờ kiểm tra (3600000 ms)
    public void capNhatTrangThaiKhuyenMaiHetHan() {
        LocalDateTime now = LocalDateTime.now();
        List<KhuyenMai> ds = promotionRepository.findAll();

        for (KhuyenMai km : ds) {
            if (km.getNgayKetThuc().isBefore(now) && !"Đã kết thúc".equalsIgnoreCase(km.getTrangThai())) {
                km.setTrangThai("Đã kết thúc");
                promotionRepository.save(km);
            }
        }
    }

}
