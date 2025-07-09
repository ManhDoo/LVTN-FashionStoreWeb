package com.example.FashionStoreBE.controller;

import com.example.FashionStoreBE.dto.request.PromotionRequest;
import com.example.FashionStoreBE.model.KhuyenMai;
import com.example.FashionStoreBE.model.SanPham;
import com.example.FashionStoreBE.repository.PromotionRepository;
import com.example.FashionStoreBE.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promotion")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<KhuyenMai> taoKhuyenMai(@RequestBody PromotionRequest request) {
        return ResponseEntity.ok(promotionService.createPromotion(request));
    }

    // Gán sản phẩm vào chương trình khuyến mãi
    @PutMapping("/gan-san-pham")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SanPham> ganSanPham(
            @RequestParam int maSanPham,
            @RequestParam int maKhuyenMai
    ) {
        return ResponseEntity.ok(promotionService.ganSanPhamVaoKhuyenMai(maSanPham, maKhuyenMai));
    }

    @GetMapping("/product")
    public ResponseEntity<List<SanPham>> getAllPromotionProducts() {
        List<SanPham> products = promotionService.getAllProductPromotion();
        return ResponseEntity.ok(products);
    }
}
