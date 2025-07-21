package com.example.FashionStoreBE.service;

import com.example.FashionStoreBE.dto.request.RateCommentRequest;
import com.example.FashionStoreBE.dto.response.ProductReviewSummary;
import com.example.FashionStoreBE.dto.response.ReviewResponse;

public interface RateCommentService {
    void themDanhGiaVaBinhLuan(RateCommentRequest request, int userId);
    ProductReviewSummary layTatCaDanhGiaSanPham(int maSanPham);
}
