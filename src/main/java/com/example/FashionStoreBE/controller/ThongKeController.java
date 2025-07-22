package com.example.FashionStoreBE.controller;

import com.example.FashionStoreBE.dto.response.ThongKe.TopSanPhamDTO;
import com.example.FashionStoreBE.dto.response.ThongKe.TopSanPhamThangDTO;
import com.example.FashionStoreBE.service.ThongKeService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/thong-ke")
@AllArgsConstructor
public class ThongKeController {
    private final ThongKeService thongKeService;

    @GetMapping("/doanh-thu-theo-thang")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Map<String, Object>> getDoanhThuTheoThang(@RequestParam int year) {
        return thongKeService.thongKeTheoThang(year);
    }

    @GetMapping("/top-3-san-pham")
    @PreAuthorize("hasRole('ADMIN')")
    public List<TopSanPhamThangDTO> getTopSanPham(
            @RequestParam(required = false) String phai,
            @RequestParam int year) {
        return thongKeService.getTop3SanPham(phai, year);
    }
}
