package com.example.FashionStoreBE.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailRequest {
    private int maKichCo; // ID of KichCo
    private int maMau;    // ID of MauSac
    private List<String> hinhAnh; // Images for this specific variant
    private double giaThem; // Additional price for this variant
    private int tonKho;    // Stock quantity for this variant
}
