package com.example.FashionStoreBE.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PhieuDoiTraResponse {
    private int maPhieu;
    private int maDonHang;
    private String loai;
    private String lyDo;
    private String trangThai;
    private LocalDateTime ngayTao;
    private List<ChiTietDto> chiTietDoiTra;

    @Data
    public static class ChiTietDto {
        private int chiTietDonHangId;
        private String tenSanPham;
        private List<String> hinhAnh;
        private int soLuongDoi;
        private String lyDoChiTiet;
        private List<String> hinhAnhMinhChung;
    }
}
