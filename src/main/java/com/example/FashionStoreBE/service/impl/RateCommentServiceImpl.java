package com.example.FashionStoreBE.service.impl;

import com.example.FashionStoreBE.dto.request.RateCommentRequest;
import com.example.FashionStoreBE.dto.response.ProductReviewSummary;
import com.example.FashionStoreBE.dto.response.ReviewResponse;
import com.example.FashionStoreBE.model.*;
import com.example.FashionStoreBE.repository.CommentRepository;
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
    private final CommentRepository binhLuanRepo;
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
        danhGia.setNgayDanhGia(LocalDateTime.now());
        danhGiaRepo.save(danhGia);

        // Lưu bình luận
        BinhLuan binhLuan = new BinhLuan();
        binhLuan.setKhachHang(khachHang);
        binhLuan.setChiTietDonHang(chiTiet);
        binhLuan.setNoiDung(request.getNoiDung());
        binhLuan.setHinhAnh(request.getHinhAnh());
        binhLuan.setNgayBinhLuan(LocalDateTime.now());
        binhLuanRepo.save(binhLuan);
    }

    @Override
    public ProductReviewSummary layTatCaDanhGiaSanPham(int maSanPham) {
        List<DanhGia> danhGias = danhGiaRepo.findByChiTietDonHang_ChiTietSanPham_SanPham_MaSanPham(maSanPham);
        List<BinhLuan> binhLuans = binhLuanRepo.findByChiTietDonHang_ChiTietSanPham_SanPham_MaSanPham(maSanPham);

        List<ReviewResponse> responseList = new ArrayList<>();

        Map<Integer, Integer> thongKeSoSao = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            thongKeSoSao.put(i, 0);
        }

        for (DanhGia dg : danhGias) {
            BinhLuan bl = binhLuans.stream()
                    .filter(b -> b.getChiTietDonHang().getId() == dg.getChiTietDonHang().getId() &&
                            b.getKhachHang().getMaKhachHang() == dg.getKhachHang().getMaKhachHang())
                    .findFirst()
                    .orElse(null);

            ChiTietSanPham chiTiet = dg.getChiTietDonHang().getChiTietSanPham();
            SanPham sp = chiTiet.getSanPham();

            ReviewResponse review = new ReviewResponse();
            review.setHoTenKhachHang(dg.getKhachHang().getHoTen());
            review.setSoSao(dg.getSoSao());
            review.setNgayDanhGia(dg.getNgayDanhGia());

            if (bl != null) {
                review.setNoiDung(bl.getNoiDung());
                review.setHinhAnh(bl.getHinhAnh());
            }

            review.setTenSanPham(sp.getTensp());
            review.setMauSac(chiTiet.getMauSac().getTenMau());

            review.setHinhAnhSanPham(sp.getHinhAnh());

            responseList.add(review);
            thongKeSoSao.put(dg.getSoSao(), thongKeSoSao.getOrDefault(dg.getSoSao(), 0) + 1);
        }

        double diemTB = danhGias.stream()
                .mapToInt(DanhGia::getSoSao)
                .average()
                .orElse(0.0);

        return new ProductReviewSummary(danhGias.size(), diemTB, responseList, thongKeSoSao);
    }


}
