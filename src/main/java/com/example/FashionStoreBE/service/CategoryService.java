package com.example.FashionStoreBE.service;

import com.example.FashionStoreBE.model.DanhMuc;

import java.util.List;

public interface CategoryService {
    List<DanhMuc> getAllCategories();
    DanhMuc getCategoryById(int id);
    DanhMuc createCategory(DanhMuc danhMuc);
    DanhMuc updateCategory(int id, DanhMuc danhMuc);
    void deleteCategory(int id);
    List<DanhMuc> getDanhMucByPhai(String phai);
}
