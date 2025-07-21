package com.example.FashionStoreBE.service;

import com.example.FashionStoreBE.model.SanPham;
import org.springframework.data.domain.Page;

import java.util.List;

public interface FavoriteItemsService {
    void themYeuThich(int maKhachHang, int maSanPham);
    void xoaYeuThich(int maKhachHang, int maSanPham);
    public Page<SanPham> getAllFavoriteProducts(int maKhachHang, int page);
}
