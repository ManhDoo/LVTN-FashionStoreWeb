package com.example.FashionStoreBE.controller;

import com.example.FashionStoreBE.config.TokenProvider;
import com.example.FashionStoreBE.model.SanPham;
import com.example.FashionStoreBE.service.FavoriteItemsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorite")
@RequiredArgsConstructor
public class FavoriteItemsController {

    private final FavoriteItemsService favoriteItemsService;
    private final TokenProvider tokenProvider;

    private int extractUserIdFromRequest(HttpServletRequest request) {
        String jwt = request.getHeader("Authorization");
        return tokenProvider.getUserIdFromToken(jwt);
    }

    @PostMapping("/add")
    public String themSanPhamYeuThich(
            HttpServletRequest request,
            @RequestParam int maSanPham
    ) {
        int maKhachHang = extractUserIdFromRequest(request);
        favoriteItemsService.themYeuThich(maKhachHang, maSanPham);
        return "Đã thêm sản phẩm vào danh sách yêu thích";
    }

    @DeleteMapping("/remove")
    public String xoaSanPhamYeuThich(
            HttpServletRequest request,
            @RequestParam int maSanPham
    ) {
        int maKhachHang = extractUserIdFromRequest(request);
        favoriteItemsService.xoaYeuThich(maKhachHang, maSanPham);
        return "Đã xóa sản phẩm khỏi danh sách yêu thích";
    }

    @GetMapping("/list")
    public Page<SanPham> layDanhSachSanPhamYeuThich(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page) {
        int maKhachHang = extractUserIdFromRequest(request);
        return favoriteItemsService.getAllFavoriteProducts(maKhachHang, page);
    }


}
