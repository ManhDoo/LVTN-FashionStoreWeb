package com.example.FashionStoreBE.service.impl;

import com.example.FashionStoreBE.model.DanhMuc;
import com.example.FashionStoreBE.repository.CategoryRepository;
import com.example.FashionStoreBE.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriesServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<DanhMuc> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public DanhMuc getCategoryById(int id) {
        return categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục" + id));
    }

    @Override
    public DanhMuc createCategory(DanhMuc danhMuc) {
        return categoryRepository.save(danhMuc);
    }

    @Override
    public DanhMuc updateCategory(int id, DanhMuc danhMuc) {
        DanhMuc cate = getCategoryById(id);
        cate.setTendm(danhMuc.getTendm());
        cate.setPhai(danhMuc.getPhai());
        cate.setMota(danhMuc.getMota());
        return categoryRepository.save(cate);
    }

    @Override
    public void deleteCategory(int id) {
        DanhMuc existingCategory = getCategoryById(id);
        categoryRepository.delete(existingCategory);
    }

    @Override
    public List<DanhMuc> getDanhMucByPhai(String phai) {
        if (!phai.equals("Nam") && !phai.equals("Nu")) {
            throw new IllegalArgumentException("Phái phải là 'Nam' hoặc 'Nữ'");
        }
        return categoryRepository.findByPhai(phai);
    }
}
