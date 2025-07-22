package com.example.FashionStoreBE.controller;

import com.example.FashionStoreBE.dto.request.PromotionRequest;
import com.example.FashionStoreBE.exception.ApiException;
import com.example.FashionStoreBE.model.KhuyenMai;
import com.example.FashionStoreBE.model.SanPham;
import com.example.FashionStoreBE.repository.PromotionRepository;
import com.example.FashionStoreBE.service.PromotionService;
import com.google.api.gax.rpc.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> xoaKhuyenMai(@PathVariable int id) {
        try {
            promotionService.xoaKhuyenMai(id);
            return ResponseEntity.ok("Đã xóa khuyến mãi thành công.");
        } catch (ApiException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<KhuyenMai> suaKhuyenMai(@PathVariable int id, @RequestBody PromotionRequest request) {
        return ResponseEntity.ok(promotionService.suaKhuyenMai(id, request));
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

    @PutMapping("/go-khuyen-mai/{maSanPham}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SanPham> goBoKhuyenMai(@PathVariable int maSanPham) {
        SanPham sp = promotionService.goBoKhuyenMaiKhoiSanPham(maSanPham);
        return ResponseEntity.ok(sp);
    }


    @GetMapping("/product")
    public ResponseEntity<List<SanPham>> getAllPromotionProducts() {
        List<SanPham> products = promotionService.getAllProductPromotion();
        return ResponseEntity.ok(products);
    }

    @GetMapping
    public ResponseEntity<List<KhuyenMai>> getAllPromotions() {
        List<KhuyenMai> products = promotionService.getAllPromotions();
        return ResponseEntity.ok(products);
    }
}
