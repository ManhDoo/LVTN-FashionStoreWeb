package com.example.FashionStoreBE.service;

import com.example.FashionStoreBE.dto.response.ProductColorGroupResponse;
import com.example.FashionStoreBE.dto.response.ProductDetailResponse;
import com.example.FashionStoreBE.model.ChiTietSanPham;

import java.util.List;

public interface ProductDetailService {
    List<ChiTietSanPham> getBySanPhamId(int sanPhamId);

//    ProductDetailResponse getGroupedByColor(int sanPhamId);
}
