package com.example.FashionStoreBE.controller;

import com.example.FashionStoreBE.model.DanhMuc;
import com.example.FashionStoreBE.model.KichCo;
import com.example.FashionStoreBE.service.CartService;
import com.example.FashionStoreBE.service.SizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/size")
public class SizeController {

    @Autowired
    private SizeService sizeService;

    @GetMapping
    public ResponseEntity<List<KichCo>> getAllSizes() {
        List<KichCo> sizes = sizeService.getAllSizes();
        return new ResponseEntity<>(sizes, HttpStatus.OK);
    }
}
