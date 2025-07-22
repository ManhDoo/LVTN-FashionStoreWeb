package com.example.FashionStoreBE.service.impl;

import com.example.FashionStoreBE.dto.response.ThongKe.DoanhThuThangDTO;
import com.example.FashionStoreBE.dto.response.ThongKe.TopSanPhamDTO;
import com.example.FashionStoreBE.dto.response.ThongKe.TopSanPhamThangDTO;
import com.example.FashionStoreBE.repository.OrderDetailRepository;
import com.example.FashionStoreBE.repository.OrderRepository;
import com.example.FashionStoreBE.service.ThongKeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class ThongKeServiceImpl implements ThongKeService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    @Override
    public List<Map<String, Object>> thongKeTheoThang(int year) {
        List<DoanhThuThangDTO> rawData = orderRepository.thongKeDoanhThuTheoThang(year);
        Map<Integer, DoanhThuThangDTO> map = new HashMap<>();
        rawData.forEach(item -> map.put(item.getThang(), item));

        List<Map<String, Object>> result = new ArrayList<>();
        for (int thang = 1; thang <= 12; thang++) {
            DoanhThuThangDTO dto = map.get(thang);
            Map<String, Object> monthStat = new HashMap<>();
            monthStat.put("thang", thang);
            monthStat.put("doanhThu", dto != null ? dto.getDoanhThu() : 0.0);
            monthStat.put("soLuongSanPham", dto != null ? dto.getSoLuongSanPham() : 0);
            monthStat.put("soDon", dto != null ? dto.getSoDon() : 0);
            result.add(monthStat);
        }
        return result;
    }

    @Override
    public List<TopSanPhamThangDTO> getTop3SanPham(String phai, int year) {
        List<Object[]> rawData = orderDetailRepository.getTopSanPhamTheoThang(
                (phai == null || phai.isBlank()) ? null : phai,
                year
        );

        Map<Integer, List<TopSanPhamDTO>> groupedByThang = new HashMap<>();

        for (Object[] row : rawData) {
            String ten = (String) row[0];
            int sl = ((BigDecimal) row[1]).intValue();
            int thang = ((Integer) row[2]);
            String gioiTinh = (String) row[3];

            TopSanPhamDTO dto = new TopSanPhamDTO() {
                public String getTenSanPham() { return ten; }
                public int getSoLuongMua() { return sl; }
                public int getThang() { return thang; }
                public String getGioiTinh() { return gioiTinh; }
            };

            groupedByThang.computeIfAbsent(thang, k -> new ArrayList<>()).add(dto);
        }

        List<TopSanPhamThangDTO> result = new ArrayList<>();

        for (int thang = 1; thang <= 12; thang++) {
            List<TopSanPhamDTO> list = groupedByThang.getOrDefault(thang, new ArrayList<>());
            List<TopSanPhamDTO> top3 = list.stream().limit(3).toList();
            result.add(new TopSanPhamThangDTO(thang, top3));
        }

        return result;
    }

}
