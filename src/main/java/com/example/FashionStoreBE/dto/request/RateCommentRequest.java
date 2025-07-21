package com.example.FashionStoreBE.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class RateCommentRequest {
    private int chiTietDonHangId;
    private int soSao;
    private String noiDung;

    // URL ảnh đã upload lên Cloudinary (từ FE)
    private List<String> hinhAnh;
}
