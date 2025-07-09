package com.example.FashionStoreBE.service.impl;

import com.example.FashionStoreBE.dto.response.ProductColorGroupResponse;
import com.example.FashionStoreBE.dto.response.ProductDetailResponse;
import com.example.FashionStoreBE.dto.response.ProductSizeResponse;
import com.example.FashionStoreBE.dto.response.PromotionResponse;
import com.example.FashionStoreBE.exception.ResourceNotFoundException;
import com.example.FashionStoreBE.model.ChiTietSanPham;
import com.example.FashionStoreBE.model.KhuyenMai;
import com.example.FashionStoreBE.model.SanPham;
import com.example.FashionStoreBE.repository.ProductDetailRopository;
import com.example.FashionStoreBE.repository.ProductRepository;
import com.example.FashionStoreBE.service.ProductDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductDetailServiceImpl implements ProductDetailService {

    @Autowired
    private ProductDetailRopository productDetailRopository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<ChiTietSanPham> getBySanPhamId(int sanPhamId) {
        return productDetailRopository.findBySanPham_MaSanPham(sanPhamId);
    }

//    @Override
//    public ProductDetailResponse getGroupedByColor(int sanPhamId) {
//        SanPham sanPham = productRepository.findById(sanPhamId)
//                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));
//
//        List<ChiTietSanPham> chiTietList = productDetailRopository.findBySanPham_MaSanPham(sanPhamId);
//
//        // Khuyến mãi
//        KhuyenMai km = sanPham.getKhuyenMai();
//        PromotionResponse promotion = km == null ? null : new PromotionResponse(
//                km.getMaKhuyenMai(),
//                km.getTenKhuyenMai(),
//                km.getGiaTriGiam(),
//                km.getHinhThucGiam(),
//                km.getLoaiKhuyenMai(),
//                km.getMoTa(),
//                km.getNgayBatDau(),
//                km.getNgayKetThuc(),
//                km.getTrangThai()
//        );
//
//        // Group theo màu
//        Map<Integer, ProductColorGroupResponse> grouped = new LinkedHashMap<>();
//        for (ChiTietSanPham ct : chiTietList) {
//            int maMau = ct.getSanPhamMauSac().getId();
//            String tenMau = ct.getSanPhamMauSac().getMauSac().getTenMau().trim();
//            List<String> hinhAnhMau = ct.getSanPhamMauSac().getHinhAnh();
//
//            grouped.putIfAbsent(maMau, new ProductColorGroupResponse(
//                    maMau,
//                    tenMau,
//                    hinhAnhMau,
//                    new ArrayList<>(),
//                    promotion
//            ));
//
//            grouped.get(maMau).getSizes().add(new ProductSizeResponse(
//                    ct.getKichCo().getMaKichCo(),
//                    ct.getKichCo().getTenKichCo(),
//                    ct.getGiaThem(),
//                    ct.getTonKho()
//            ));
//        }
//
//        return new ProductDetailResponse(
//                sanPham.getMaSanPham(),
//                sanPham.getTensp(),
//                sanPham.getHinhAnh(),
//                sanPham.getGiaGoc(),
//                sanPham.getMoTa(),
//                sanPham.getNgayTao(),
//                sanPham.getDanhMuc().getTendm(),
//                new ArrayList<>(grouped.values())
//        );
//    }

}
