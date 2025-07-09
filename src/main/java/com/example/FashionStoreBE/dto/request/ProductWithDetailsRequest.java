package com.example.FashionStoreBE.dto.request;

import com.example.FashionStoreBE.model.SanPham;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductWithDetailsRequest {
    private SanPham sanPham;
    private List<ProductDetailRequest> chiTietSanPhamDTOs;
}
