package com.example.FashionStoreBE.repository;

import com.example.FashionStoreBE.model.ChiTietDonHang;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<ChiTietDonHang, Integer> {
}
