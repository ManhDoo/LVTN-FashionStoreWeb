package com.example.FashionStoreBE.controller;

import com.example.FashionStoreBE.dto.request.ProductWithDetailsRequest;
import com.example.FashionStoreBE.model.SanPham;
import com.example.FashionStoreBE.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<Page<SanPham>> getAllProducts(@RequestParam(defaultValue = "0") int page) {
        Page<SanPham> products = productService.getAllProducts(page);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SanPham> getProductById(@PathVariable("id") int id) {
        SanPham product = productService.getProductById(id);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SanPham> createProduct(@RequestBody SanPham sanPham) {
        SanPham createdProduct = productService.createProduct(sanPham);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @PostMapping("/with-details")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SanPham> createProductWithDetails(
            @RequestBody ProductWithDetailsRequest request) {
        SanPham savedProduct = productService.createProductWithDetails(
                request.getSanPham(), request.getChiTietSanPhamDTOs());
        return ResponseEntity.ok(savedProduct);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SanPham> updateProduct(@PathVariable("id") int id, @RequestBody ProductWithDetailsRequest request) {
        SanPham updatedProduct = productService.updateProduct(id, request.getSanPham(), request.getChiTietSanPhamDTOs());
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") int id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(Map.of("message", "Xóa sản phẩm thành công"));
    }

    @GetMapping("/phai/{phai}")
    public ResponseEntity<List<SanPham>> getProductsByPhai(@PathVariable("phai") String phai) {
        List<SanPham> products = productService.getProductsByPhai(phai);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/search")
    public List<SanPham> searchProducts(@RequestParam("keyword") String keyword) {
        return productService.searchProducts(keyword);
    }

    @GetMapping("/danhmuc/{maDanhMuc}")
    public ResponseEntity<List<SanPham>> getProductsByDanhMuc(@PathVariable int maDanhMuc) {
        List<SanPham> products = productService.getProductsByDanhMuc(maDanhMuc);
        return ResponseEntity.ok(products);
    }

}