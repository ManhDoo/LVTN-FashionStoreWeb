package com.example.FashionStoreBE.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductReviewSummary {
    private int tongSoDanhGia;
    private double diemTrungBinh;
    private List<ReviewResponse> danhSachDanhGia;

    private Map<Integer, Integer> thongKeSoSao;
}
