package com.example.FashionStoreBE.service.impl;

import com.example.FashionStoreBE.dto.response.BillDetailResponse;
import com.example.FashionStoreBE.dto.response.BillResponse;
import com.example.FashionStoreBE.exception.ResourceNotFoundException;
import com.example.FashionStoreBE.model.ChiTietSanPham;
import com.example.FashionStoreBE.model.DonHang;
import com.example.FashionStoreBE.model.HoaDon;
import com.example.FashionStoreBE.repository.BillRepository;
import com.example.FashionStoreBE.service.BillService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class BillServiceImpl implements BillService {

    private final BillRepository billRepository;

    @Override
    public Page<BillResponse> getAllHoaDon(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("ngayTao").descending());
        Page<HoaDon> hoaDonPage = billRepository.findAll(pageable);

        List<BillResponse> responseList = hoaDonPage.getContent().stream().map(hd -> {
            BillResponse dto = new BillResponse();
            dto.setMaHoaDon(hd.getMaHoaDon());
            dto.setPhiGiaoHang(hd.getPhiGiaoHang());
            dto.setThanhTien(hd.getThanhTien());
            dto.setTongGia(hd.getTongGia());
            dto.setGhiChu(hd.getGhiChu());
            dto.setTrangThai(hd.getTrangThai());
            dto.setNgayTao(hd.getNgayTao());

            if (hd.getDonDatHang() != null) {
                DonHang donHang = hd.getDonDatHang();
                dto.setMaDonHang(donHang.getMaDonHang());
                dto.setTenNguoiNhan(donHang.getTenNguoiNhan());
                dto.setSoDienThoai(donHang.getSoDienThoaiNguoiNhan());
                dto.setDiaChi(donHang.getDuong() + ", " + donHang.getXa() + ", " + donHang.getHuyen() + ", " + donHang.getTinh());
            }

            return dto;
        }).toList();

        return new PageImpl<>(responseList, pageable, hoaDonPage.getTotalElements());
    }

    @Override
    public BillResponse getBillById(int id) {
        HoaDon hd = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn với ID: " + id));

        BillResponse dto = new BillResponse();
        dto.setMaHoaDon(hd.getMaHoaDon());
        dto.setPhiGiaoHang(hd.getPhiGiaoHang());
        dto.setThanhTien(hd.getThanhTien());
        dto.setTongGia(hd.getTongGia());
        dto.setGhiChu(hd.getGhiChu());
        dto.setTrangThai(hd.getTrangThai());
        dto.setNgayTao(hd.getNgayTao());

        if (hd.getDonDatHang() != null) {
            DonHang donHang = hd.getDonDatHang();
            dto.setMaDonHang(donHang.getMaDonHang());
            dto.setTenNguoiNhan(donHang.getTenNguoiNhan());
            dto.setSoDienThoai(donHang.getSoDienThoaiNguoiNhan());
            dto.setDiaChi(donHang.getDuong() + ", " + donHang.getXa() + ", " + donHang.getHuyen() + ", " + donHang.getTinh());

            List<BillDetailResponse> chiTietDonHangList = donHang.getChiTietDonHangs().stream().map(chiTiet -> {
                BillDetailResponse detailDto = new BillDetailResponse();
                ChiTietSanPham chiTietSanPham = chiTiet.getChiTietSanPham();

                // Lấy thông tin sản phẩm
                detailDto.setTenSanPham(chiTietSanPham.getSanPham().getTensp());
                detailDto.setMauSac(chiTietSanPham.getMauSac().getTenMau());
                detailDto.setKichCo(chiTietSanPham.getKichCo().getTenKichCo());
                detailDto.setSoLuong(chiTiet.getSoLuong());

                return detailDto;
            }).toList();

            // Gán danh sách chi tiết đơn hàng vào BillResponse
            dto.setChiTietDonHang(chiTietDonHangList);
        }

        return dto;
    }

}
