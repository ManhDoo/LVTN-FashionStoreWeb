package com.example.FashionStoreBE.controller;

import com.example.FashionStoreBE.model.PhuongThucThanhToan;
import com.example.FashionStoreBE.service.PaymentMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/payment")
public class PaymentMethodController {

    @Autowired
    private PaymentMethodService paymentMethodService;

    @GetMapping
    public ResponseEntity<List<PhuongThucThanhToan>> getAllPaymentMethod(){
        List<PhuongThucThanhToan> pttt = paymentMethodService.getAllPaymentMethods();
        return new ResponseEntity<>(pttt, HttpStatus.OK);
    }
}
