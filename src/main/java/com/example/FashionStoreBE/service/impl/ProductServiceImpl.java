package com.example.FashionStoreBE.service.impl;

import com.example.FashionStoreBE.dto.request.ProductDetailRequest;
import com.example.FashionStoreBE.exception.ProductDeleteException;
import com.example.FashionStoreBE.exception.ResourceNotFoundException;
import com.example.FashionStoreBE.model.*;
import com.example.FashionStoreBE.repository.*;
import com.example.FashionStoreBE.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductDetailRopository productDetailRopository;

    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private SizeRepository sizeRepository;

    @Autowired
    private ColorProductRepository colorProductRepository;


    @Override
    public Page<SanPham> getAllProducts(int page) {
        int pageSize = 10;
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("ngayTao").descending());
        return productRepository.findAll(pageable);
    }

    @Override
    public SanPham  getProductById(int id) {
        return productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với mã: " + id));
    }

    @Override
    public SanPham createProduct(SanPham sanPham) {
        sanPham.setNgayTao(LocalDateTime.now());
        sanPham.setNgayCapNhat(LocalDateTime.now());
        return productRepository.save(sanPham);
    }

//    @Override
//    @Transactional
//    public SanPham createProductWithDetails(SanPham sanPham, List<ProductDetailRequest> chiTietSanPhamDTOs) {
//        if (sanPham == null || chiTietSanPhamDTOs == null || chiTietSanPhamDTOs.isEmpty()) {
//            throw new IllegalArgumentException("Sản phẩm và chi tiết sản phẩm không được để trống");
//        }
//
//        if (sanPham.getTensp() == null || sanPham.getTensp().trim().isEmpty()) {
//            throw new IllegalArgumentException("Tên sản phẩm không được để trống");
//        }
//        if (sanPham.getGiaGoc() <= 0) {
//            throw new IllegalArgumentException("Giá gốc phải lớn hơn 0");
//        }
//        if (sanPham.getMoTa() == null || sanPham.getMoTa().trim().isEmpty()) {
//            throw new IllegalArgumentException("Mô tả sản phẩm không được để trống");
//        }
//
//        sanPham.setNgayTao(LocalDateTime.now());
//        sanPham.setNgayCapNhat(LocalDateTime.now());
//
//        SanPham savedProduct = productRepository.save(sanPham);
//
//        // Tạo map lưu lại các SanPhamMauSac đã được tạo để tránh tạo trùng
//        Map<Integer, SanPhamMauSac> colorMap = new HashMap<>();
//
//        for (ProductDetailRequest dto : chiTietSanPhamDTOs) {
//            KichCo kichCo = sizeRepository.findById(dto.getMaKichCo())
//                    .orElseThrow(() -> new IllegalArgumentException("Kích cỡ không tồn tại: " + dto.getMaKichCo()));
//
//            MauSac mauSac = colorRepository.findById(dto.getMaMau())
//                    .orElseThrow(() -> new IllegalArgumentException("Màu sắc không tồn tại: " + dto.getMaMau()));
//
//            // Nếu màu này chưa được tạo trong map thì tạo mới SanPhamMauSac
//            SanPhamMauSac sanPhamMauSac = colorMap.get(dto.getMaMau());
//            if (sanPhamMauSac == null) {
//                sanPhamMauSac = new SanPhamMauSac();
//                sanPhamMauSac.setSanPham(savedProduct);
//                sanPhamMauSac.setMauSac(mauSac);
//                sanPhamMauSac.setHinhAnh(dto.getHinhAnh()); // ✅ chỉ cần gắn ảnh ở đây
//                sanPhamMauSac = colorProductRepository.save(sanPhamMauSac);
//
//                colorMap.put(dto.getMaMau(), sanPhamMauSac);
//            }
//
//            // Tạo chi tiết sản phẩm (biến thể size)
//            ChiTietSanPham chiTiet = new ChiTietSanPham();
//            chiTiet.setSanPham(savedProduct);
//            chiTiet.setSanPhamMauSac(sanPhamMauSac); // dùng liên kết mới
//            chiTiet.setKichCo(kichCo);
//            chiTiet.setGiaThem(dto.getGiaThem());
//            chiTiet.setTonKho(dto.getTonKho());
//
//            productDetailRopository.save(chiTiet);
//        }
//
//        return savedProduct;
//    }

    @Override
    @Transactional
    public SanPham createProductWithDetails(SanPham sanPham, List<ProductDetailRequest> chiTietSanPhamDTOs) {
        // Validate input
        if (sanPham == null || chiTietSanPhamDTOs == null || chiTietSanPhamDTOs.isEmpty()) {
            throw new IllegalArgumentException("Sản phẩm và chi tiết sản phẩm không được để trống");
        }

        // Validate required fields
        if (sanPham.getTensp() == null || sanPham.getTensp().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên sản phẩm không được để trống");
        }
        if (sanPham.getGiaGoc() <= 0) {
            throw new IllegalArgumentException("Giá gốc phải lớn hơn 0");
        }
        if (sanPham.getMoTa() == null || sanPham.getMoTa().trim().isEmpty()) {
            throw new IllegalArgumentException("Mô tả sản phẩm không được để trống");
        }

        // Set creation and update timestamps
        sanPham.setNgayTao(LocalDateTime.now());
        sanPham.setNgayCapNhat(LocalDateTime.now());

        // Save the product first
        SanPham savedProduct = productRepository.save(sanPham);

        // Create and save ChiTietSanPham entries
        for (ProductDetailRequest dto : chiTietSanPhamDTOs) {
            ChiTietSanPham chiTiet = new ChiTietSanPham();
            chiTiet.setSanPham(savedProduct);

            // Fetch KichCo by ID
            KichCo kichCo = sizeRepository.findById(dto.getMaKichCo())
                    .orElseThrow(() -> new IllegalArgumentException("Kích cỡ không tồn tại: " + dto.getMaKichCo()));
            chiTiet.setKichCo(kichCo);

            // Fetch MauSac by ID
            MauSac mauSac = colorRepository.findById(dto.getMaMau())
                    .orElseThrow(() -> new IllegalArgumentException("Màu sắc không tồn tại: " + dto.getMaMau()));
            chiTiet.setMauSac(mauSac);

            // Set other fields
            chiTiet.setHinhAnh(dto.getHinhAnh());
            chiTiet.setGiaThem(dto.getGiaThem());
            chiTiet.setTonKho(dto.getTonKho());

            // Save ChiTietSanPham
            productDetailRopository.save(chiTiet);
        }

        return savedProduct;
    }

    @Override
    @Transactional
    public SanPham updateProduct(int id, SanPham sanPham, List<ProductDetailRequest> chiTietSanPhamDTOs) {
        // Tìm sản phẩm hiện có
        SanPham existingProduct = getProductById(id);

        // Không cho phép cập nhật các thông tin chính của sản phẩm
        // Chỉ giữ lại thời gian cập nhật
        existingProduct.setNgayCapNhat(LocalDateTime.now());
        SanPham updatedProduct = productRepository.save(existingProduct);

        // Lấy danh sách chi tiết sản phẩm hiện có
        List<ChiTietSanPham> existingDetails = productDetailRopository.findBySanPham(existingProduct);

        // Tạo map để so sánh chi tiết sản phẩm theo maMau và maKichCo
        Map<String, ChiTietSanPham> existingDetailsMap = existingDetails.stream()
                .collect(Collectors.toMap(
                        detail -> detail.getMauSac().getMaMau() + "-" + detail.getKichCo().getMaKichCo(),
                        detail -> detail
                ));

        // Kiểm tra xem các chi tiết sản phẩm hiện có có nằm trong đơn hàng không
        for (ChiTietSanPham chiTiet : existingDetails) {
            boolean isInOrder = productDetailRopository.existsInChiTietDonHangByChiTietSanPham(chiTiet.getId());
            if (isInOrder && chiTietSanPhamDTOs.stream().noneMatch(dto ->
                    dto.getMaMau() == chiTiet.getMauSac().getMaMau() &&
                            dto.getMaKichCo() == chiTiet.getKichCo().getMaKichCo())) {
                throw new ProductDeleteException("Không thể xóa chi tiết sản phẩm vì nó đang tồn tại trong đơn hàng");
            }
        }

        // Xử lý danh sách chi tiết sản phẩm mới
        for (ProductDetailRequest dto : chiTietSanPhamDTOs) {
            String key = dto.getMaMau() + "-" + dto.getMaKichCo();
            ChiTietSanPham existingDetail = existingDetailsMap.get(key);

            if (existingDetail != null) {
                // Cập nhật tồn kho cho chi tiết sản phẩm hiện có
                existingDetail.setTonKho(dto.getTonKho());
                productDetailRopository.save(existingDetail);
                existingDetailsMap.remove(key); // Xóa khỏi map để không xóa sau này
            } else {
                // Thêm mới chi tiết sản phẩm
                ChiTietSanPham newDetail = new ChiTietSanPham();
                newDetail.setSanPham(updatedProduct);

                KichCo kichCo = sizeRepository.findById(dto.getMaKichCo())
                        .orElseThrow(() -> new IllegalArgumentException("Kích cỡ không tồn tại: " + dto.getMaKichCo()));
                newDetail.setKichCo(kichCo);

                MauSac mauSac = colorRepository.findById(dto.getMaMau())
                        .orElseThrow(() -> new IllegalArgumentException("Màu sắc không tồn tại: " + dto.getMaMau()));
                newDetail.setMauSac(mauSac);

                newDetail.setHinhAnh(dto.getHinhAnh());
                newDetail.setGiaThem(dto.getGiaThem());
                newDetail.setTonKho(dto.getTonKho());

                productDetailRopository.save(newDetail);
            }
        }

        // Xóa các chi tiết sản phẩm không còn trong danh sách DTO
//        for (ChiTietSanPham detailToDelete : existingDetailsMap.values()) {
//            boolean isInOrder = productDetailRopository.existsInChiTietDonHangByChiTietSanPham(detailToDelete.getId());
//            if (isInOrder) {
//                throw new ProductDeleteException("Không thể xóa chi tiết sản phẩm vì nó đang tồn tại trong đơn hàng");
//            }
//            productDetailRopository.delete(detailToDelete);
//        }

        return updatedProduct;
    }


    @Override
    public void deleteProduct(int id) {
        SanPham existingProduct = getProductById(id);

        // Lấy danh sách chi tiết sản phẩm
        List<ChiTietSanPham> chiTietList = productDetailRopository.findBySanPham(existingProduct);

        // Kiểm tra từng chi tiết sản phẩm có nằm trong đơn hàng không
        for (ChiTietSanPham chiTiet : chiTietList) {
            boolean isInOrder = productDetailRopository.existsInChiTietDonHangByChiTietSanPham(chiTiet.getId());
            if (isInOrder) {
                throw new ProductDeleteException("Không thể xóa sản phẩm vì có chi tiết đang tồn tại trong đơn hàng");
            }
        }

        // Xóa chi tiết sản phẩm trước
        productDetailRopository.deleteAll(chiTietList);

        // Xóa sản phẩm
        productRepository.delete(existingProduct);
    }

    @Override
    public List<SanPham> getProductsByPhai(String phai) {
        if (!phai.equals("Nam") && !phai.equals("Nu")) {
            throw new IllegalArgumentException("Phái phải là 'Nam' hoặc 'Nữ'");
        }
        return productRepository.findByDanhMucPhai(phai);
    }

    @Override
    public List<SanPham> searchProducts(String keyword) {
        return productRepository.searchByKeyword(keyword);
    }

    @Override
    public List<SanPham> getProductsByDanhMuc(int maDanhMuc) {
        return productRepository.findByDanhMuc_MaDanhMuc(maDanhMuc);
    }

}
