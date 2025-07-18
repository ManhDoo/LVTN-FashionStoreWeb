package com.example.FashionStoreBE.repository;

import com.example.FashionStoreBE.model.MauSac;
import com.example.FashionStoreBE.model.SanPham;
import com.example.FashionStoreBE.model.SanPhamMauSac;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ColorProductRepository extends JpaRepository<SanPhamMauSac, Integer> {
    Optional<SanPhamMauSac> findBySanPhamAndMauSac(SanPham sanPham, MauSac mauSac);
}
