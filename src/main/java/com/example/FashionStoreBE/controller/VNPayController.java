package com.example.FashionStoreBE.controller;

import com.example.FashionStoreBE.service.VNPayService;
import com.example.FashionStoreBE.service.impl.OrderServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@RestController
@RequestMapping("api/vnpay")
public class VNPayController {

    @Autowired
    private VNPayService vnPayService;

    @GetMapping("/return")
    public void handleVNPayReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        vnPayService.handleVNPayReturn(request, response);
    }
}