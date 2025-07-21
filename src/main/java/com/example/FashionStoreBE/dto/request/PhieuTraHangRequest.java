package com.example.FashionStoreBE.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class PhieuTraHangRequest {
    private int maDonHang;
    private String loai; // "DOI" hoặc "TRA"
    private String lyDo;
    private double phiDoiTra;
    private List<Item> items;

    @Data
    public static class Item {
        private int chiTietDonHangId;
        private List<String> hinhAnh;
        private int soLuong;
        private String lyDoChiTiet;
    }
}
