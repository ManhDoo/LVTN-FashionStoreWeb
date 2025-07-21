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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    private final ProductDetailRopository chiTietSanPhamRepo;

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
        phieu.setPhiDoiTra(request.getPhiDoiTra());

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
    public Page<PhieuDoiTraResponse> getAllReturnRequestsByUser(int userId, int page, int size) {
        Page<PhieuDoiTra> pageResult = phieuDoiTraRepository
                .findAllByDonHang_KhachHang_MaKhachHang(
                        userId,
                        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "ngayTao"))
                );

        List<PhieuDoiTraResponse> dtoList = mapToDtoList(pageResult.getContent());

        return new PageImpl<>(dtoList, pageResult.getPageable(), pageResult.getTotalElements());
    }

    @Override
    public Page<PhieuDoiTraResponse> getAllReturnRequests(int page, int size) {
        Page<PhieuDoiTra> pageResult = phieuDoiTraRepository
                .findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "ngayTao")));

        List<PhieuDoiTraResponse> dtoList = mapToDtoList(pageResult.getContent());

        return new PageImpl<>(dtoList, pageResult.getPageable(), pageResult.getTotalElements());
    }

    private List<PhieuDoiTraResponse> mapToDtoList(List<PhieuDoiTra> phieuList) {
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
    @Transactional
    public String updateReturnRequestStatus(int maPhieu, String newStatus) {
        PhieuDoiTra phieu = phieuDoiTraRepository.findById(maPhieu)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu đổi/trả"));

        // Validate new status
        if (!List.of("CHO_XAC_NHAN", "DA_XAC_NHAN", "DANG_XU_LY", "HOAN_THANH", "TU_CHOI").contains(newStatus)) {
            throw new ApiException("Trạng thái không hợp lệ");
        }

        // If status is being updated to HOAN_THANH
        if ("HOAN_THANH".equals(newStatus)) {
            // Handle inventory for TRA (return) requests
            boolean laLoiSanPham = "LOI_SAN_PHAM".equalsIgnoreCase(phieu.getLyDo());
            boolean canHoanLaiTonKho = !laLoiSanPham;

            if (canHoanLaiTonKho) {
                for (ChiTietDoiTra chiTiet : phieu.getChiTietDoiTras()) {
                    ChiTietSanPham ctsp = chiTiet.getChiTietDonHang().getChiTietSanPham();
                    ctsp.setTonKho(ctsp.getTonKho() + chiTiet.getSoLuongDoi());
                    chiTietSanPhamRepo.save(ctsp);
                }
            }


            // Handle DOI (exchange) requests: Create a new order
            if ("DOI".equalsIgnoreCase(phieu.getLoai())) {
                DonHang originalOrder = phieu.getDonHang();
                KhachHang khachHang = originalOrder.getKhachHang();
                if (khachHang == null) {
                    throw new ApiException("Không thể tạo đơn hàng mới: Đơn hàng gốc không có thông tin khách hàng");
                }

                // Create new order
                DonHang newOrder = new DonHang();
                newOrder.setKhachHang(khachHang);
                newOrder.setTenNguoiNhan(originalOrder.getTenNguoiNhan());
                newOrder.setSoDienThoaiNguoiNhan(originalOrder.getSoDienThoaiNguoiNhan());
                newOrder.setEmailNguoiNhan(originalOrder.getEmailNguoiNhan());
                newOrder.setDuong(originalOrder.getDuong());
                newOrder.setXa(originalOrder.getXa());
                newOrder.setHuyen(originalOrder.getHuyen());
                newOrder.setTinh(originalOrder.getTinh());
                newOrder.setPhiGiaoHang(phieu.getPhiDoiTra());
                newOrder.setPhuongThucThanhToan(originalOrder.getPhuongThucThanhToan());
                newOrder.setNgayTao(LocalDateTime.now());
                newOrder.setNgayCapNhat(LocalDateTime.now());
                newOrder.setTrangThai("CHO_XAC_NHAN");
                newOrder.setCoThanhToan(false);
                newOrder.setCoYeuCauDoiTra(false);

                // Save the new order to generate ID
                newOrder = donHangRepo.save(newOrder);

                int tongSoLuong = 0;
                double tongGia = 0;

                // Create order details based on return request items
                for (ChiTietDoiTra chiTietDoiTra : phieu.getChiTietDoiTras()) {
                    ChiTietDonHang originalChiTiet = chiTietDoiTra.getChiTietDonHang();
                    ChiTietSanPham ctsp = originalChiTiet.getChiTietSanPham();

                    // Check inventory
                    if (ctsp.getTonKho() < chiTietDoiTra.getSoLuongDoi()) {
                        throw new ApiException("Không đủ hàng tồn kho cho sản phẩm: " + ctsp.getId());
                    }

                    // Create new order detail
                    ChiTietDonHang newChiTiet = new ChiTietDonHang();
                    newChiTiet.setDonDatHang(newOrder);
                    newChiTiet.setChiTietSanPham(ctsp);
                    newChiTiet.setSoLuong(chiTietDoiTra.getSoLuongDoi());
                    newChiTiet.setDonGia(originalChiTiet.getDonGia());
                    newChiTiet.setSoTienGiamGia(0);
                    chiTietDonHangRepo.save(newChiTiet);

                    // Update inventory
                    ctsp.setTonKho(ctsp.getTonKho() - chiTietDoiTra.getSoLuongDoi());
                    chiTietSanPhamRepo.save(ctsp);

                    tongSoLuong += chiTietDoiTra.getSoLuongDoi();
                    tongGia += originalChiTiet.getDonGia() * chiTietDoiTra.getSoLuongDoi();
                }

                // Update total quantity and price
                newOrder.setTongSoLuong(tongSoLuong);
                newOrder.setTongGia(tongGia);
                donHangRepo.save(newOrder);
            }
        }

        phieu.setTrangThai(newStatus);
        phieuDoiTraRepository.save(phieu);

        return "Cập nhật trạng thái phiếu đổi/trả #" + maPhieu + " thành " + newStatus;
    }

}
