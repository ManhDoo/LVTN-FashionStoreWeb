package com.example.FashionStoreBE.dto.response;

import com.example.FashionStoreBE.model.SanPham;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductColorGroupResponse {
    private int maMau;
    private String tenMau;
    private List<String> hinhAnh; // hình ảnh theo màu
    private List<ProductSizeResponse> sizes; // danh sách size theo màu
    private PromotionResponse khuyenMai; // 🎯 Thêm thông tin khuyến mãi của sản phẩm
}
