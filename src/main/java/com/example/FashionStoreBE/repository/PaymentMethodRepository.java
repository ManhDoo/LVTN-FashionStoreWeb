package com.example.FashionStoreBE.repository;

import com.example.FashionStoreBE.model.PhuongThucThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentMethodRepository extends JpaRepository<PhuongThucThanhToan, Integer> {
}
