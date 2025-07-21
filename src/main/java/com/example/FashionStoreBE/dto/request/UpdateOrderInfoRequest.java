package com.example.FashionStoreBE.dto.request;

import lombok.Data;

@Data
public class UpdateOrderInfoRequest {
    private String tenNguoiNhan;
    private int soDienThoaiNguoiNhan;
    private String duong;
    private String xa;
    private String huyen;
    private String tinh;
}
