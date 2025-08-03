package com.example.FashionStoreBE.controller;

import com.example.FashionStoreBE.config.TokenProvider;
import com.example.FashionStoreBE.dto.request.RateCommentRequest;
import com.example.FashionStoreBE.dto.response.ProductReviewSummary;
import com.example.FashionStoreBE.dto.response.ReviewCommentResponse;
import com.example.FashionStoreBE.dto.response.ReviewResponse;
import com.example.FashionStoreBE.service.RateCommentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
@AllArgsConstructor
public class RateCommentController {

    private final RateCommentService rateCommentService;
    private final TokenProvider tokenProvider;

    private int extractUserIdFromRequest(HttpServletRequest request) {
        String jwt = request.getHeader("Authorization");
        return tokenProvider.getUserIdFromToken(jwt);
    }


    @PostMapping
    public ResponseEntity<?> themDanhGiaVaBinhLuan(@RequestBody RateCommentRequest request, HttpServletRequest httpServletRequest) {
        int userId = extractUserIdFromRequest(httpServletRequest);
        rateCommentService.themDanhGiaVaBinhLuan(request, userId);
        return ResponseEntity.ok("Đánh giá và bình luận thành công!");
    }

    @GetMapping("/{maSanPham}")
    public ResponseEntity<ProductReviewSummary> getAllReviewsByProduct(@PathVariable int maSanPham) {
        return ResponseEntity.ok(rateCommentService.layTatCaDanhGiaSanPham(maSanPham));
    }

    @GetMapping("/chua-duyet")
    public ResponseEntity<List<ReviewResponse>> layTatCaChuaDuyet() {
        return ResponseEntity.ok(rateCommentService.layDanhSachDanhGiaChuaDuyet());
    }

    @PutMapping("/admin/duyet-danh-gia/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> duyetDanhGia(@PathVariable int id) {
        rateCommentService.duyetDanhGia(id);
        return ResponseEntity.ok("Đã duyệt đánh giá");
    }

}
