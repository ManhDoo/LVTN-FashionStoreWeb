package com.example.FashionStoreBE.service.impl;

import com.example.FashionStoreBE.model.PhuongThucThanhToan;
import com.example.FashionStoreBE.repository.PaymentMethodRepository;
import com.example.FashionStoreBE.service.PaymentMethodService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentMethodServiceImpl implements PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;

    public PaymentMethodServiceImpl(PaymentMethodRepository paymentMethodRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
    }

    @Override
    public List<PhuongThucThanhToan> getAllPaymentMethods() {
        return paymentMethodRepository.findAll();
    }
}
