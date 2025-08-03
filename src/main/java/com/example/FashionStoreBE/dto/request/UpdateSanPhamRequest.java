package com.example.FashionStoreBE.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateSanPhamRequest {
    private String tensp;
    private Float giaGoc;
    private String moTa;
    private Double trongLuong;
    private List<String> hinhAnh;
    private Integer maDanhMuc;
    private Integer maKhuyenMai;
    private List<ChiTietSanPhamDTO> chiTietSanPhams;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChiTietSanPhamDTO {
        private Integer id;
        private Integer tonKho;
        private Double giaThem;
        private List<String> hinhAnh;
        private Integer maKichCo;
        private Integer maMau;
    }
}
