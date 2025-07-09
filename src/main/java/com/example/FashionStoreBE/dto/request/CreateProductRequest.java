package com.example.FashionStoreBE.dto.request;

import java.util.List;

public class CreateProductRequest {
    private String tensp;
    private float giaGoc;
    private String moTa;
    private int maDanhMuc;
    private Integer maKhuyenMai; // có thể null
    private List<ProductDetailRequest> chiTietSanPham;
}
