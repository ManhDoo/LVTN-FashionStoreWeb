package com.example.FashionStoreBE.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSizeResponse {
    private int maKichCo;
    private String tenKichCo;
    private double giaThem;
    private int tonKho;
}
