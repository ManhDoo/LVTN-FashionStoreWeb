package com.example.FashionStoreBE.service.impl;

import com.example.FashionStoreBE.exception.ApiException;
import com.example.FashionStoreBE.model.KhachHang;
import com.example.FashionStoreBE.model.SanPham;
import com.example.FashionStoreBE.model.SanPhamYeuThich;
import com.example.FashionStoreBE.repository.FavoriteItemsRepository;
import com.example.FashionStoreBE.repository.ProductRepository;
import com.example.FashionStoreBE.repository.UserRepository;
import com.example.FashionStoreBE.service.FavoriteItemsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteItemsServiceImpl implements FavoriteItemsService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final FavoriteItemsRepository favoriteItemsRepository;

    @Override
    public void themYeuThich(int maKhachHang, int maSanPham) {
        KhachHang khachHang = userRepository.findById(maKhachHang)
                .orElseThrow(() -> new ApiException("Không tìm thấy khách hàng"));

        SanPham sanPham = productRepository.findById(maSanPham)
                .orElseThrow(() -> new ApiException("Không tìm thấy sản phẩm"));

        // Kiểm tra đã thích chưa
        boolean exists = favoriteItemsRepository.findByKhachHangAndSanPham(khachHang, sanPham).isPresent();
        if (exists) {
            throw new ApiException("Sản phẩm đã được yêu thích");
        }

        SanPhamYeuThich yeuThich = new SanPhamYeuThich();
        yeuThich.setKhachHang(khachHang);
        yeuThich.setSanPham(sanPham);
        yeuThich.setNgayThem(LocalDateTime.now());

        favoriteItemsRepository.save(yeuThich);
    }

    @Override
    public void xoaYeuThich(int maKhachHang, int maSanPham) {
        KhachHang khachHang = userRepository.findById(maKhachHang)
                .orElseThrow(() -> new ApiException("Không tìm thấy khách hàng"));

        SanPham sanPham = productRepository.findById(maSanPham)
                .orElseThrow(() -> new ApiException("Không tìm thấy sản phẩm"));

        SanPhamYeuThich yeuThich = favoriteItemsRepository.findByKhachHangAndSanPham(khachHang, sanPham)
                .orElseThrow(() -> new ApiException("Sản phẩm chưa được yêu thích"));

        favoriteItemsRepository.delete(yeuThich);
    }

    @Override
    public Page<SanPham> getAllFavoriteProducts(int maKhachHang, int page) {
        int pageSize = 10;
        Pageable pageable = PageRequest.of(page, pageSize);

        KhachHang khachHang = userRepository.findById(maKhachHang)
                .orElseThrow(() -> new ApiException("Không tìm thấy khách hàng"));

        Page<SanPhamYeuThich> favoriteItemsPage = favoriteItemsRepository.findAllByKhachHang(khachHang, pageable);

        return favoriteItemsPage.map(SanPhamYeuThich::getSanPham);
    }

}
