package com.example.FashionStoreBE.service;

import com.example.FashionStoreBE.model.PhuongThucThanhToan;
import com.example.FashionStoreBE.model.SanPham;

import java.util.List;

public interface PaymentMethodService {
    List<PhuongThucThanhToan> getAllPaymentMethods();
}
