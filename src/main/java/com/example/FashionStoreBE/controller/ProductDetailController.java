package com.example.FashionStoreBE.controller;

import com.example.FashionStoreBE.dto.response.ProductColorGroupResponse;
import com.example.FashionStoreBE.dto.response.ProductDetailResponse;
import com.example.FashionStoreBE.model.ChiTietSanPham;
import com.example.FashionStoreBE.service.ProductDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductDetailController {

    @Autowired
    private ProductDetailService productDetailService;

    @GetMapping("/{id}/details")
    public ResponseEntity<List<ChiTietSanPham>> getChiTietSanPhamBySanPhamId(@PathVariable("id") int id) {
        List<ChiTietSanPham> chiTietList = productDetailService.getBySanPhamId(id);
        return ResponseEntity.ok(chiTietList);
    }

//    @GetMapping("/v2/{id}/details")
//    public ResponseEntity<ProductDetailResponse> getProductDetailsGroupedByColor(@PathVariable("id") int id) {
//        ProductDetailResponse groupedDetails = productDetailService.getGroupedByColor(id);
//        return ResponseEntity.ok(groupedDetails);
//    }
}
