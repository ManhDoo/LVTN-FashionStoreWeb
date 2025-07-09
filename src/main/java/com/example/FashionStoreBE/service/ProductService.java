package com.example.FashionStoreBE.service;


import com.example.FashionStoreBE.dto.request.ProductDetailRequest;
import com.example.FashionStoreBE.model.SanPham;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {
    Page<SanPham> getAllProducts(int page);
    SanPham getProductById(int id);
    SanPham createProduct(SanPham danhMuc);
    SanPham createProductWithDetails(SanPham sanPham, List<ProductDetailRequest> chiTietSanPhamDTOs);
    SanPham updateProduct(int id, SanPham sanPham, List<ProductDetailRequest> chiTietSanPhamDTOs);
    void deleteProduct(int id);
    List<SanPham> getProductsByPhai(String phai);
    List<SanPham> searchProducts(String keyword);
    List<SanPham> getProductsByDanhMuc(int maDanhMuc);

}
