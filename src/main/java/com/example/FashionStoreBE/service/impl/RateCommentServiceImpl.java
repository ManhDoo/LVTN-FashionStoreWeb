package com.example.FashionStoreBE.service.impl;

import com.example.FashionStoreBE.dto.request.RateCommentRequest;
import com.example.FashionStoreBE.dto.response.ProductReviewSummary;
import com.example.FashionStoreBE.dto.response.ReviewCommentResponse;
import com.example.FashionStoreBE.dto.response.ReviewResponse;
import com.example.FashionStoreBE.model.*;
import com.example.FashionStoreBE.repository.OrderDetailRepository;
import com.example.FashionStoreBE.repository.RateRepository;
import com.example.FashionStoreBE.repository.UserRepository;
import com.example.FashionStoreBE.service.RateCommentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RateCommentServiceImpl implements RateCommentService {

    private final RateRepository danhGiaRepo;
    private final UserRepository khachHangRepo;
    private final OrderDetailRepository chiTietDonHangRepo;

    @Override
    public void themDanhGiaVaBinhLuan(RateCommentRequest request, int userId) {
        KhachHang khachHang = khachHangRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        ChiTietDonHang chiTiet = chiTietDonHangRepo.findById(request.getChiTietDonHangId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết đơn hàng"));

        // Kiểm tra nếu đã đánh giá rồi
//        if (danhGiaRepo.existsByKhachHang_MaKhachHangAndChiTietDonHang_Id(khachHang.getMaKhachHang(), chiTiet.getId())) {
//            throw new RuntimeException("Bạn đã đánh giá sản phẩm này rồi!");
//        }

        // Kiểm tra nếu đã bình luận rồi
//        if (binhLuanRepo.existsByKhachHang_MaKhachHangAndChiTietDonHang_Id(khachHang.getMaKhachHang(), chiTiet.getId())) {
//            throw new RuntimeException("Bạn đã bình luận sản phẩm này rồi!");
//        }

        // Lưu đánh giá
        DanhGia danhGia = new DanhGia();
        danhGia.setKhachHang(khachHang);
        danhGia.setChiTietDonHang(chiTiet);
        danhGia.setSoSao(request.getSoSao());
        danhGia.setNoiDung(request.getNoiDung());
        danhGia.setHinhAnh(request.getHinhAnh());
        danhGia.setNgayDanhGia(LocalDateTime.now());
        danhGiaRepo.save(danhGia);
    }

    @Override
    public ProductReviewSummary layTatCaDanhGiaSanPham(int maSanPham) {
        List<DanhGia> danhGias = danhGiaRepo
                .findByChiTietDonHang_ChiTietSanPham_SanPham_MaSanPhamAndDuyetTrue(maSanPham);

        List<ReviewResponse> responseList = new ArrayList<>();

        Map<Integer, Integer> thongKeSoSao = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            thongKeSoSao.put(i, 0);
        }

        for (DanhGia dg : danhGias) {
            ChiTietSanPham chiTiet = dg.getChiTietDonHang().getChiTietSanPham();
            SanPham sp = chiTiet.getSanPham();

            ReviewResponse review = new ReviewResponse();
            review.setId(dg.getId());
            review.setHoTenKhachHang(dg.getKhachHang().getHoTen());
            review.setSoSao(dg.getSoSao());
            review.setNgayDanhGia(dg.getNgayDanhGia());
            review.setNoiDung(dg.getNoiDung());
            review.setHinhAnh(dg.getHinhAnh());
            review.setTenSanPham(sp.getTensp());
            review.setMauSac(chiTiet.getMauSac().getTenMau());
            review.setHinhAnhSanPham(sp.getHinhAnh());

            responseList.add(review);
            thongKeSoSao.put(dg.getSoSao(), thongKeSoSao.get(dg.getSoSao()) + 1);
        }

        double diemTB = danhGias.stream()
                .mapToInt(DanhGia::getSoSao)
                .average()
                .orElse(0.0);

        return new ProductReviewSummary(danhGias.size(), diemTB, responseList, thongKeSoSao);
    }


    @Override
    public void duyetDanhGia(int id) {
        DanhGia dg = danhGiaRepo.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy đánh giá"));
        dg.setDuyet(true);
        danhGiaRepo.save(dg);
    }

    @Override
    public List<ReviewResponse> layDanhSachDanhGiaChuaDuyet() {
        List<DanhGia> danhGias = danhGiaRepo.findByDuyetFalse();

        List<ReviewResponse> responseList = new ArrayList<>();

        for (DanhGia dg : danhGias) {
            ChiTietSanPham chiTiet = dg.getChiTietDonHang().getChiTietSanPham();
            SanPham sp = chiTiet.getSanPham();

            ReviewResponse review = new ReviewResponse();
            review.setId(dg.getId());
            review.setHoTenKhachHang(dg.getKhachHang().getHoTen());
            review.setSoSao(dg.getSoSao());
            review.setNgayDanhGia(dg.getNgayDanhGia());
            review.setNoiDung(dg.getNoiDung());
            review.setHinhAnh(dg.getHinhAnh());
            review.setTenSanPham(sp.getTensp());
            review.setMauSac(chiTiet.getMauSac().getTenMau());
            review.setHinhAnhSanPham(sp.getHinhAnh());

            responseList.add(review);
        }

        return responseList;
    }




}
