package com.example.FashionStoreBE.dto.response.ThongKe;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TopSanPhamThangDTO {
    private int thang;
    private List<TopSanPhamDTO> topSanPhams;
}
