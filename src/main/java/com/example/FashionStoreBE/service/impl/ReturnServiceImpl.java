package com.example.FashionStoreBE.service.impl;

import com.example.FashionStoreBE.dto.request.PhieuTraHangRequest;
import com.example.FashionStoreBE.dto.response.PhieuDoiTraResponse;
import com.example.FashionStoreBE.exception.ApiException;
import com.example.FashionStoreBE.exception.ProductDeleteException;
import com.example.FashionStoreBE.exception.ResourceNotFoundException;
import com.example.FashionStoreBE.model.*;
import com.example.FashionStoreBE.repository.*;
import com.example.FashionStoreBE.service.ReturnService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ReturnServiceImpl implements ReturnService {

    private final OrderRepository donHangRepo;
    private final OrderDetailRepository chiTietDonHangRepo;
    private final PhieuDoiTraRepository phieuDoiTraRepository;

    @Override
    @Transactional
    public String createReturnRequest(PhieuTraHangRequest request, int userId) {
        DonHang donHang = donHangRepo.findById(request.getMaDonHang())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng"));

        // Kiểm tra quyền
        if (donHang.getKhachHang() == null || donHang.getKhachHang().getMaKhachHang() != userId) {
            throw new ProductDeleteException("Bạn không có quyền đổi/trả đơn hàng này.");
        }

        // ✅ Kiểm tra nếu quá 7 ngày
        LocalDateTime ngayGiaoHang = donHang.getNgayGiao();
        LocalDateTime ngayHienTai = LocalDateTime.now();

        if (ngayGiaoHang.plusDays(7).isBefore(ngayHienTai)) {
            throw new ApiException("Đơn hàng đã quá hạn đổi/trả (7 ngày).");
        }
        if (donHang.isCoYeuCauDoiTra()) {
            throw new ApiException("Đơn hàng này đã gửi yêu cầu đổi/trả trước đó.");
        }

        if (!donHang.getTrangThai().equals("DA_GIAO") ){
            throw new ApiException("Đơn hàng đã giao mới được đổi trả");
        }

        PhieuDoiTra phieu = new PhieuDoiTra();
        phieu.setDonHang(donHang);
        phieu.setLoai(request.getLoai().toUpperCase());
        try {
            ReturnReason reason = ReturnReason.valueOf(request.getLyDo().toUpperCase());
            phieu.setLyDo(reason.name());
        } catch (IllegalArgumentException e) {
            throw new ApiException("Lý do đổi trả không hợp lệ");
        }

        phieu.setNgayTao(LocalDateTime.now());
        phieu.setTrangThai("CHO_XAC_NHAN");

        List<ChiTietDoiTra> chiTietList = new ArrayList<>();

        for (PhieuTraHangRequest.Item item : request.getItems()) {
            ChiTietDonHang chiTietDon = chiTietDonHangRepo.findById(item.getChiTietDonHangId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chi tiết đơn hàng"));

            if (item.getSoLuong() > chiTietDon.getSoLuong()) {
                throw new ApiException("Số lượng đổi/trả vượt quá số lượng đã mua");
            }

            // Nếu là đổi hàng, hoàn lại tồn kho
//            if ("DOI".equalsIgnoreCase(request.getLoai())) {
//                ChiTietSanPham ctsp = chiTietDon.getChiTietSanPham();
//                ctsp.setTonKho(ctsp.getTonKho() + item.getSoLuong());
//                chiTietSanPhamRepo.save(ctsp);
//            }

            ChiTietDoiTra chiTietDoiTra = new ChiTietDoiTra();
            chiTietDoiTra.setPhieuDoiTra(phieu);
            chiTietDoiTra.setChiTietDonHang(chiTietDon);
            chiTietDoiTra.setHinhAnhMinhChung(item.getHinhAnh());
            chiTietDoiTra.setSoLuongDoi(item.getSoLuong());
            chiTietDoiTra.setLyDoChiTiet(item.getLyDoChiTiet());

            chiTietList.add(chiTietDoiTra);
        }

        phieu.setChiTietDoiTras(chiTietList);
        donHang.setCoYeuCauDoiTra(true);
        phieuDoiTraRepository.save(phieu);

        return "Tạo yêu cầu " + request.getLoai() + " thành công với mã đơn #" + donHang.getMaDonHang();
    }

    @Override
    public List<PhieuDoiTraResponse> getAllReturnRequestsByUser(int userId) {
        List<PhieuDoiTra> phieuList = phieuDoiTraRepository.findAllByUserId(userId);

        return phieuList.stream().map(phieu -> {
            PhieuDoiTraResponse dto = new PhieuDoiTraResponse();
            dto.setMaPhieu(phieu.getMaPhieu());
            dto.setLoai(phieu.getLoai());
            dto.setLyDo(phieu.getLyDo());
            dto.setTrangThai(phieu.getTrangThai());
            dto.setNgayTao(phieu.getNgayTao());
            dto.setMaDonHang(phieu.getDonHang().getMaDonHang());

            List<PhieuDoiTraResponse.ChiTietDto> chiTietDtos = phieu.getChiTietDoiTras().stream().map(ct -> {
                PhieuDoiTraResponse.ChiTietDto chiTietDto = new PhieuDoiTraResponse.ChiTietDto();
                chiTietDto.setChiTietDonHangId(ct.getChiTietDonHang().getId());
                chiTietDto.setTenSanPham(ct.getChiTietDonHang().getChiTietSanPham().getSanPham().getTensp());
                chiTietDto.setHinhAnh(ct.getChiTietDonHang().getChiTietSanPham().getSanPham().getHinhAnh());
                chiTietDto.setSoLuongDoi(ct.getSoLuongDoi());
                chiTietDto.setLyDoChiTiet(ct.getLyDoChiTiet());
                chiTietDto.setHinhAnhMinhChung(ct.getHinhAnhMinhChung());
                return chiTietDto;
            }).toList();

            dto.setChiTietDoiTra(chiTietDtos);
            return dto;
        }).toList();
    }

    @Override
    public List<PhieuDoiTraResponse> getAllReturnRequests() {
        List<PhieuDoiTra> phieuList = phieuDoiTraRepository.findAll();

        return phieuList.stream().map(phieu -> {
            PhieuDoiTraResponse dto = new PhieuDoiTraResponse();
            dto.setMaPhieu(phieu.getMaPhieu());
            dto.setLoai(phieu.getLoai());
            dto.setLyDo(phieu.getLyDo());
            dto.setTrangThai(phieu.getTrangThai());
            dto.setNgayTao(phieu.getNgayTao());
            dto.setMaDonHang(phieu.getDonHang().getMaDonHang());

            List<PhieuDoiTraResponse.ChiTietDto> chiTietDtos = phieu.getChiTietDoiTras().stream().map(ct -> {
                PhieuDoiTraResponse.ChiTietDto chiTietDto = new PhieuDoiTraResponse.ChiTietDto();
                chiTietDto.setChiTietDonHangId(ct.getChiTietDonHang().getId());
                chiTietDto.setTenSanPham(ct.getChiTietDonHang().getChiTietSanPham().getSanPham().getTensp());
                chiTietDto.setHinhAnh(ct.getChiTietDonHang().getChiTietSanPham().getSanPham().getHinhAnh());
                chiTietDto.setSoLuongDoi(ct.getSoLuongDoi());
                chiTietDto.setLyDoChiTiet(ct.getLyDoChiTiet());
                chiTietDto.setHinhAnhMinhChung(ct.getHinhAnhMinhChung());
                return chiTietDto;
            }).toList();

            dto.setChiTietDoiTra(chiTietDtos);
            return dto;
        }).toList();
    }

}
