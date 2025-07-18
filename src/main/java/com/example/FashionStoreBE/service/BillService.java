package com.example.FashionStoreBE.service;

import com.example.FashionStoreBE.dto.response.BillResponse;
import org.springframework.data.domain.Page;

public interface BillService {
    Page<BillResponse> getAllHoaDon(int page, int size);
    BillResponse getBillById(int id);

}
