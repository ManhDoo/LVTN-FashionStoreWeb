package com.example.FashionStoreBE.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailResponse {
    private int maSanPham;
    private String tensp;
    private List<String> hinhAnh;
    private float giaGoc;
    private String moTa;
    private LocalDateTime ngayTao;
    private String danhMuc;

    private List<ProductColorGroupResponse> colorGroups;
}
