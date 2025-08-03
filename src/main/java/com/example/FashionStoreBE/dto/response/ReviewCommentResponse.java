package com.example.FashionStoreBE.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewCommentResponse {
    private int id;
    private String loai; // "DANH_GIA" hoặc "BINH_LUAN"
    private String noiDung;
    private Integer soSao; // chỉ có nếu là đánh giá
    private String tenKhachHang;
    private String tenSanPham;
    private LocalDateTime thoiGian;
}
