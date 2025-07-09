package com.example.FashionStoreBE.service;

import com.example.FashionStoreBE.dto.request.PhieuTraHangRequest;

public interface ReturnService {
    String createReturnRequest(PhieuTraHangRequest request, int userId);
}
