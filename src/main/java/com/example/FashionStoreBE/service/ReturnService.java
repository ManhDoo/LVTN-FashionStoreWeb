package com.example.FashionStoreBE.service;

import com.example.FashionStoreBE.dto.request.PhieuTraHangRequest;
import com.example.FashionStoreBE.dto.response.PhieuDoiTraResponse;

import java.util.List;

public interface ReturnService {
    String createReturnRequest(PhieuTraHangRequest request, int userId);
    List<PhieuDoiTraResponse> getAllReturnRequestsByUser(int userId);
    List<PhieuDoiTraResponse> getAllReturnRequests();

}
