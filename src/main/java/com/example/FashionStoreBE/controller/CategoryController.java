package com.example.FashionStoreBE.controller;

import com.example.FashionStoreBE.model.DanhMuc;
import com.example.FashionStoreBE.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // Get all categories (accessible to all authenticated users)
    @GetMapping
    public ResponseEntity<List<DanhMuc>> getAllCategories() {
        List<DanhMuc> categories = categoryService.getAllCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    // Get a category by ID (accessible to all authenticated users)
    @GetMapping("/{id}")
    public ResponseEntity<DanhMuc> getCategoryById(@PathVariable("id") int id) {
        DanhMuc category = categoryService.getCategoryById(id);
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    // Create a new category (ADMIN only)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DanhMuc> createCategory(@RequestBody DanhMuc danhMuc) {
        DanhMuc createdCategory = categoryService.createCategory(danhMuc);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }

    // Update a category (ADMIN only)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DanhMuc> updateCategory(@PathVariable("id") int id, @RequestBody DanhMuc danhMuc) {
        DanhMuc updatedCategory = categoryService.updateCategory(id, danhMuc);
        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
    }

    // Delete a category (ADMIN only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable("id") int id) {
        categoryService.deleteCategory(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/phai/{phai}")
    public ResponseEntity<List<DanhMuc>> getDanhMucByPhai(@PathVariable("phai") String phai) {
        List<DanhMuc> list = categoryService.getDanhMucByPhai(phai);
        return ResponseEntity.ok(list);
    }
}