package com.example.FashionStoreBE.controller;

import com.example.FashionStoreBE.model.KichCo;
import com.example.FashionStoreBE.model.MauSac;
import com.example.FashionStoreBE.service.ColorService;
import com.example.FashionStoreBE.service.SizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/color")
public class ColorController {
    @Autowired
    private ColorService colorService;

    @GetMapping
    public ResponseEntity<List<MauSac>> getAllColors() {
        List<MauSac> sizes = colorService.getAllColors();
        return new ResponseEntity<>(sizes, HttpStatus.OK);
    }
}
