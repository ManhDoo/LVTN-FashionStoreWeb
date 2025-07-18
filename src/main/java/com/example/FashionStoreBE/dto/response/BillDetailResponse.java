package com.example.FashionStoreBE.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillDetailResponse {
    private String tenSanPham;
    private String mauSac;
    private String kichCo;
    private int soLuong;

}
