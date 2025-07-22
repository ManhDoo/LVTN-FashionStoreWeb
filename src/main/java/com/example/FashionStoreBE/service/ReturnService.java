package com.example.FashionStoreBE.service;

import com.example.FashionStoreBE.dto.request.PhieuTraHangRequest;
import com.example.FashionStoreBE.dto.response.PhieuDoiTraResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ReturnService {
    String createReturnRequest(PhieuTraHangRequest request, int userId);
    Page<PhieuDoiTraResponse> getAllReturnRequestsByUser(int userId, int page, int size);
    Page<PhieuDoiTraResponse> getAllReturnRequests(String trangThai, int page, int size);
    String updateReturnRequestStatus(int maPhieu, String newStatus);


}
