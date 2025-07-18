package com.example.FashionStoreBE.model;

public enum ReturnReason {
    THIEU_HANG("Thiếu hàng"),
    LOI_SAN_PHAM("Lỗi sản phẩm"),
    KHAC_MO_TA("Khác mô tả"),
    GUI_SAI_HANG("Người bán gửi sai hàng"),
    KHONG_CON_NHU_CAU("Không còn nhu cầu"),
    KHAC("Khác");

    private final String description;

    ReturnReason(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
