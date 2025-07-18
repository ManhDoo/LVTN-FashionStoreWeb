package com.example.FashionStoreBE.controller;

import com.example.FashionStoreBE.dto.response.BillResponse;
import com.example.FashionStoreBE.service.BillService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bill")
@AllArgsConstructor
public class BillController {

    private final BillService billService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<BillResponse>> getAllHoaDon(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<BillResponse> result = billService.getAllHoaDon(page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BillResponse> getBillById(@PathVariable int id) {
        BillResponse response = billService.getBillById(id);
        return ResponseEntity.ok(response);
    }
}
