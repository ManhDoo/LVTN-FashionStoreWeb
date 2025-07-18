package com.example.FashionStoreBE.service.impl;

import com.example.FashionStoreBE.config.VNPayConfig;
import com.example.FashionStoreBE.dto.request.GuestOrderRequest;
import com.example.FashionStoreBE.dto.response.OrderDetailResponse;
import com.example.FashionStoreBE.exception.ApiException;
import com.example.FashionStoreBE.exception.ForbidenExceeption;
import com.example.FashionStoreBE.exception.ResourceNotFoundException;
import com.example.FashionStoreBE.model.*;
import com.example.FashionStoreBE.repository.*;
import com.example.FashionStoreBE.service.EmailService;
import com.example.FashionStoreBE.service.FirebaseMessagingService;
import com.example.FashionStoreBE.service.OrderService;
import com.example.FashionStoreBE.service.VNPayService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final ProductDetailRopository chiTietSanPhamRepo;
    private final OrderRepository donHangRepo;
    private final OrderDetailRepository chiTietDonHangRepo;
    private final UserRepository khachHangRepo;
    private final PaymentMethodRepository paymentMethodRepository;
    private final EmailService emailService;
    private final VNPayService vnPayService;
    private final BillRepository hoaDonRepository;
    private final FirebaseMessagingService firebaseMessagingService;


    @Override
    @Transactional
    public String placeOrder(int userId) {
        String key = "cart:user:" + userId;
        String lockKey = "lock:user:" + userId;
        String lockValue = UUID.randomUUID().toString();

        Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, 10, TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(locked)) {
            logger.warn("Order processing already in progress for user: {}", userId);
            throw new RuntimeException("Đơn hàng đang được xử lý, vui lòng chờ.");
        }

        try {
            String json = redisTemplate.opsForValue().get(key);
            if (json == null || json.isEmpty()) {
                logger.warn("Cart is empty for user: {}", userId);
                throw new RuntimeException("Giỏ hàng trống");
            }

            List<GioHang> cart = objectMapper.readValue(json, new TypeReference<List<GioHang>>() {});
            logger.debug("Cart items for user {}: {}", userId, cart);

            DonHang donHang = new DonHang();
            donHang.setNgayTao(LocalDateTime.now());
            donHang.setNgayCapNhat(LocalDateTime.now());
            donHang.setTrangThai("CHO_XAC_NHAN");
            donHang.setKhachHang(khachHangRepo.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với ID: " + userId)));
            logger.debug("Created order for user: {}", userId);

            int tongSoLuong = 0;
            double tongGia = 0;

            donHang = donHangRepo.save(donHang);
            logger.debug("Saved order with ID: {}", donHang.getMaDonHang());

            for (GioHang item : cart) {
                ChiTietSanPham chiTiet = chiTietSanPhamRepo.findById(item.getProductId())
                        .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại: " + item.getProductId()));
                logger.debug("Processing product ID: {}, quantity: {}", item.getProductId(), item.getQuantity());

                // Kiểm tra tồn kho
                if (chiTiet.getTonKho() < item.getQuantity()) {
                    logger.error("Insufficient stock for product ID: {}", item.getProductId());
                    throw new RuntimeException("Không đủ hàng cho sản phẩm: " + chiTiet.getId());
                }

                // Kiểm tra hình ảnh
                try {
                    List<String> hinhAnh = chiTiet.getHinhAnh();
                    logger.debug("Images for product ID {}: {}", item.getProductId(), hinhAnh);
                } catch (Exception e) {
                    logger.error("Error accessing images for product ID {}: {}", item.getProductId(), e.getMessage());
                    throw new RuntimeException("Lỗi khi truy cập hình ảnh sản phẩm ID " + item.getProductId() + ": " + e.getMessage());
                }

                // Tạo chi tiết đơn hàng
                ChiTietDonHang chiTietDonHang = new ChiTietDonHang();
                chiTietDonHang.setChiTietSanPham(chiTiet);
                chiTietDonHang.setDonDatHang(donHang);
                chiTietDonHang.setSoLuong(item.getQuantity());
                chiTietDonHang.setDonGia(chiTiet.getSanPham().getGiaGoc() + chiTiet.getGiaThem());
                chiTietDonHang.setSoTienGiamGia(0);
                chiTietDonHangRepo.save(chiTietDonHang);
                logger.debug("Saved order detail for product ID: {}", item.getProductId());

                // Trừ tồn kho
                chiTiet.setTonKho(chiTiet.getTonKho() - item.getQuantity());
                chiTietSanPhamRepo.save(chiTiet);
                logger.debug("Updated stock for product ID: {}, new stock: {}", item.getProductId(), chiTiet.getTonKho());

                tongSoLuong += item.getQuantity();
                tongGia += chiTietDonHang.getDonGia() * item.getQuantity();
            }

            donHang.setTongSoLuong(tongSoLuong);
            donHang.setTongGia(tongGia);
            donHangRepo.save(donHang);
            logger.debug("Updated order with total quantity: {}, total price: {}", tongSoLuong, tongGia);

            // Xóa giỏ hàng
            redisTemplate.delete(key);
            logger.debug("Cleared cart for user: {}", userId);

            return "Đặt hàng thành công với mã đơn: " + donHang.getMaDonHang();
        } catch (Exception e) {
            logger.error("Failed to place order for user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Không thể đặt hàng: " + e.getMessage(), e);
        } finally {
            String currentValue = redisTemplate.opsForValue().get(lockKey);
            if (lockValue.equals(currentValue)) {
                redisTemplate.delete(lockKey);
                logger.debug("Released lock for user: {}", userId);
            }
        }
    }

    @Override
    @Transactional
    public String placeOrderForGuest(GuestOrderRequest request) throws Exception {
        KhachHang khachHang = null;
        if (request.getMaKhachHang() != null) {
            khachHang = khachHangRepo.findById(request.getMaKhachHang())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy mã khách hàng"));
        }

        List<GioHang> cart = request.getCart();
        if (cart == null || cart.isEmpty()) {
            throw new RuntimeException("Giỏ hàng trống");
        }

        PhuongThucThanhToan pt = paymentMethodRepository.findById(request.getPttt())
                .orElseThrow(()-> new RuntimeException("Không tìm thấy phương thức thanh toán"));

        DonHang donHang = new DonHang();
        if (khachHang != null) {
            donHang.setKhachHang(khachHang);
        }
        donHang.setTenNguoiNhan(request.getTenNguoiNhan());
        donHang.setSoDienThoaiNguoiNhan(request.getSoDienThoaiNguoiNhan());
        donHang.setEmailNguoiNhan(request.getEmailNguoiNhan());
        donHang.setNgayTao(LocalDateTime.now());
        donHang.setNgayCapNhat(LocalDateTime.now());
        donHang.setPhiGiaoHang(request.getPhiGiaoHang());
        donHang.setTrangThai("CHO_XAC_NHAN");
        donHang.setCoThanhToan(false);

        // Địa chỉ giao hàng
        donHang.setDuong(request.getDuong());
        donHang.setXa(request.getXa());
        donHang.setHuyen(request.getHuyen());
        donHang.setTinh(request.getTinh());

         // Guest
        donHang.setPhuongThucThanhToan(pt);

        donHang = donHangRepo.save(donHang);



        int tongSoLuong = 0;
        double tongGia = 0;

        for (GioHang item : cart) {
            ChiTietSanPham chiTiet = chiTietSanPhamRepo.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại: " + item.getProductId()));

            if (chiTiet.getTonKho() < item.getQuantity()) {
                throw new RuntimeException("Không đủ hàng cho sản phẩm: " + chiTiet.getId());
            }

            ChiTietDonHang chiTietDonHang = new ChiTietDonHang();
            chiTietDonHang.setChiTietSanPham(chiTiet);
            chiTietDonHang.setDonDatHang(donHang);
            chiTietDonHang.setSoLuong(item.getQuantity());
            chiTietDonHang.setDonGia(chiTiet.getSanPham().getGiaGoc() + chiTiet.getGiaThem());
            chiTietDonHang.setSoTienGiamGia(0);
            chiTietDonHangRepo.save(chiTietDonHang);

            chiTiet.setTonKho(chiTiet.getTonKho() - item.getQuantity());
            chiTietSanPhamRepo.save(chiTiet);

            tongSoLuong += item.getQuantity();
            tongGia += chiTietDonHang.getDonGia() * item.getQuantity();
        }

        donHang.setTongSoLuong(tongSoLuong);
        donHang.setTongGia(tongGia);
        donHangRepo.save(donHang);

        String title = "Đơn hàng mới #" + donHang.getMaDonHang();
        String bodyfire = "Bạn vừa có đơn hàng mới. Tổng giá: " + donHang.getTongGia() + " VND";
        firebaseMessagingService.sendNotificationToTopic("admin_notifications", title, bodyfire);

        // Trả về phản hồi thành công ngay lập tức
        String successMessage = "Khách chưa đăng nhập đã đặt hàng với mã đơn: " + donHang.getMaDonHang();

        // Gửi email bất đồng bộ
        String email = request.getEmailNguoiNhan();
        String subject = "Xác nhận đơn hàng #" + donHang.getMaDonHang();
        String body = "Chào " + request.getTenNguoiNhan() + ",\n\n"
                + "Cảm ơn bạn đã đặt hàng tại FashionStore.\n"
                + "Mã đơn hàng: " + donHang.getMaDonHang() + "\n"
                + "Tổng số lượng: " + tongSoLuong + "\n"
                + "Tổng tiền: " + tongGia + " VND\n"
                + "Trạng thái: " + donHang.getTrangThai() + "\n\n"
                + "Chúng tôi sẽ sớm xử lý đơn hàng của bạn.";
        emailService.sendOrderEmail(email, subject, body);

        if ("VNPAY".equals(pt.getTenPhuongThuc())) {
            return vnPayService.createVNPayPaymentUrl(donHang, "Guest Order Payment #" + donHang.getMaDonHang());
        }

        return successMessage;
    }

    @Override
    public Page<OrderDetailResponse> getOrdersByUserId(int userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "ngayTao"));
        Page<DonHang> donHangPage = donHangRepo.findByKhachHang_MaKhachHang(userId, pageable);

        List<OrderDetailResponse> result = new ArrayList<>();

        for (DonHang donHang : donHangPage.getContent()) {
            String diaChi = donHang.getDuong() + ", " +
                    donHang.getXa() + ", " +
                    donHang.getHuyen() + ", " +
                    donHang.getTinh();

            for (ChiTietDonHang chiTiet : donHang.getChiTietDonHangs()) {
                ChiTietSanPham ctsp = chiTiet.getChiTietSanPham();
                SanPham sp = ctsp.getSanPham();

                OrderDetailResponse dto = new OrderDetailResponse();
                dto.setMaDonHang(donHang.getMaDonHang());
                dto.setTenNguoiNhan(donHang.getTenNguoiNhan());
                dto.setSoDienThoai(donHang.getSoDienThoaiNguoiNhan());
                dto.setDiaChi(diaChi);
                dto.setId(chiTiet.getId());
                dto.setMaSanPham(sp.getMaSanPham());
                dto.setTenSanPham(sp.getTensp());
                List<String> hinhAnhList = sp.getHinhAnh();
                dto.setHinhAnh(hinhAnhList != null && !hinhAnhList.isEmpty() ? hinhAnhList.get(0) : null);
                dto.setDonGia(chiTiet.getDonGia());
                dto.setSoLuong(chiTiet.getSoLuong());
                dto.setKichCo(ctsp.getKichCo().getTenKichCo());
                dto.setMauSac(ctsp.getMauSac().getTenMau());
                dto.setNgayTao(donHang.getNgayTao());
                dto.setNgayGiao(donHang.getNgayGiao());
                dto.setTrangThai(donHang.getTrangThai());
                dto.setCoThanhToan(donHang.isCoThanhToan());
                dto.setCoYeuCauDoiTra(donHang.isCoYeuCauDoiTra());
                result.add(dto);
            }
        }

        return new PageImpl<>(result, pageable, donHangPage.getTotalElements());
    }



    @Override
    @Transactional
    public String cancelOrder(int userId, int orderId) {
        DonHang donHang = donHangRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với ID: " + orderId));

        // Kiểm tra người dùng sở hữu đơn
        if (donHang.getKhachHang() == null || donHang.getKhachHang().getMaKhachHang() != userId) {
            throw new AccessDeniedException("Bạn không có quyền hủy đơn hàng này.");
        }

        if (!"CHO_XAC_NHAN".equals(donHang.getTrangThai()) && !"DA_THANH_TOAN".equals(donHang.getTrangThai())) {
            throw new ApiException("Chỉ được hủy đơn hàng khi đang ở trạng thái CHO_XAC_NHAN hoặc DA_THANH_TOAN.");
        }
        for (ChiTietDonHang chiTiet : donHang.getChiTietDonHangs()) {
            ChiTietSanPham ctsp = chiTiet.getChiTietSanPham();
            ctsp.setTonKho(ctsp.getTonKho() + chiTiet.getSoLuong());
            chiTietSanPhamRepo.save(ctsp);
        }

        donHang.setTrangThai("DA_HUY");
        donHang.setNgayCapNhat(LocalDateTime.now());
        donHangRepo.save(donHang);

        return "Đơn hàng #" + orderId + " đã được hủy thành công.";
    }

    @Override
    public Page<OrderDetailResponse> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "ngayTao"));
        Page<DonHang> donHangPage = donHangRepo.findAll(pageable);

        List<OrderDetailResponse> result = new ArrayList<>();

        for (DonHang donHang : donHangPage.getContent()) {
            String diaChi = donHang.getDuong() + ", " +
                    donHang.getXa() + ", " +
                    donHang.getHuyen() + ", " +
                    donHang.getTinh();

            for (ChiTietDonHang chiTiet : donHang.getChiTietDonHangs()) {
                ChiTietSanPham ctsp = chiTiet.getChiTietSanPham();
                SanPham sp = ctsp.getSanPham();

                OrderDetailResponse dto = new OrderDetailResponse();
                dto.setMaDonHang(donHang.getMaDonHang());
                dto.setTenNguoiNhan(donHang.getTenNguoiNhan());
                dto.setSoDienThoai(donHang.getSoDienThoaiNguoiNhan());
                dto.setDiaChi(diaChi);
                dto.setMaSanPham(sp.getMaSanPham());
                dto.setTenSanPham(sp.getTensp());

                List<String> hinhAnhList = sp.getHinhAnh();
                dto.setHinhAnh(hinhAnhList != null && !hinhAnhList.isEmpty() ? hinhAnhList.get(0) : null);

                dto.setDonGia(chiTiet.getDonGia());
                dto.setSoLuong(chiTiet.getSoLuong());
                dto.setKichCo(ctsp.getKichCo().getTenKichCo());
                dto.setMauSac(ctsp.getMauSac().getTenMau());
                dto.setNgayTao(donHang.getNgayTao());
                dto.setNgayGiao(donHang.getNgayGiao());
                dto.setTrangThai(donHang.getTrangThai());
                dto.setCoThanhToan(donHang.isCoThanhToan());
                dto.setCoYeuCauDoiTra(donHang.isCoYeuCauDoiTra());
                result.add(dto);
            }
        }

        return new PageImpl<>(result, pageable, donHangPage.getTotalElements());
    }

    @Override
    @Transactional
    public void updateOrderStatus(int orderId, String newStatus) {
        DonHang donHang = donHangRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với ID: " + orderId));

        if (newStatus.equals("DA_GIAO")){
            donHang.setNgayGiao(LocalDateTime.now());
        }

        donHang.setTrangThai(newStatus);
        donHang.setNgayCapNhat(LocalDateTime.now());

        donHangRepo.save(donHang);

        if ("DA_XAC_NHAN".equals(newStatus)) {
            // Tránh tạo trùng nếu hóa đơn đã được tạo
            boolean daCoHoaDon = hoaDonRepository.existsByDonDatHang_MaDonHang(orderId);
            if (!daCoHoaDon) {
                HoaDon hoaDon = new HoaDon();
                hoaDon.setDonDatHang(donHang);
                hoaDon.setNgayTao(LocalDateTime.now());
                if (donHang.isCoThanhToan()){
                    hoaDon.setTrangThai("DA_THANH_TOAN");
                }
                else {
                    hoaDon.setTrangThai("CHUA_THANH_TOAN");
                }

                hoaDon.setPhiGiaoHang(donHang.getPhiGiaoHang());
                hoaDon.setTongGia(donHang.getTongGia());

                // Thành tiền = tổng giá + phí giao hàng (hoặc tính tùy logic bạn)
                hoaDon.setThanhTien(donHang.getTongGia() + donHang.getPhiGiaoHang());

                hoaDon.setGhiChu("Hóa đơn cho đơn hàng #" + donHang.getMaDonHang());

                hoaDonRepository.save(hoaDon);
            }
        }

        if ("DA_HUY".equals(newStatus)) {
            hoaDonRepository.deleteByDonDatHang_MaDonHang(orderId);
        }

    }

    public Page<OrderDetailResponse> getOrdersByTrangThai(String trangThai, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("ngayTao").descending());

        Page<DonHang> donHangPage;
        if (trangThai != null && !trangThai.isEmpty()) {
            donHangPage = donHangRepo.findByTrangThai(trangThai, pageable);
        } else {
            donHangPage = donHangRepo.findAll(pageable);
        }

        // Chuyển đổi DonHang + ChiTietDonHang sang OrderDetailResponse (flatten structure)
        List<OrderDetailResponse> result = new ArrayList<>();

        for (DonHang donHang : donHangPage.getContent()) {
            String diaChi = donHang.getDuong() + ", " +
                    donHang.getXa() + ", " +
                    donHang.getHuyen() + ", " +
                    donHang.getTinh();

            for (ChiTietDonHang chiTiet : donHang.getChiTietDonHangs()) {
                ChiTietSanPham ctsp = chiTiet.getChiTietSanPham();
                SanPham sp = ctsp.getSanPham();

                OrderDetailResponse dto = new OrderDetailResponse();
                dto.setMaDonHang(donHang.getMaDonHang());
                dto.setTenNguoiNhan(donHang.getTenNguoiNhan());
                dto.setSoDienThoai(donHang.getSoDienThoaiNguoiNhan());
                dto.setDiaChi(diaChi);
                dto.setMaSanPham(sp.getMaSanPham());
                dto.setTenSanPham(sp.getTensp());

                List<String> hinhAnhList = sp.getHinhAnh();
                dto.setHinhAnh(hinhAnhList != null && !hinhAnhList.isEmpty() ? hinhAnhList.get(0) : null);

                dto.setDonGia(chiTiet.getDonGia());
                dto.setSoLuong(chiTiet.getSoLuong());
                dto.setKichCo(ctsp.getKichCo().getTenKichCo());
                dto.setMauSac(ctsp.getMauSac().getTenMau());
                dto.setNgayTao(donHang.getNgayTao());
                dto.setNgayGiao(donHang.getNgayGiao());
                dto.setTrangThai(donHang.getTrangThai());
                dto.setCoThanhToan(donHang.isCoThanhToan());
                dto.setCoYeuCauDoiTra(donHang.isCoYeuCauDoiTra());
                result.add(dto);
            }


        }
        return new PageImpl<>(result, pageable, donHangPage.getTotalElements());
    }

    @Override
    public OrderDetailResponse getOrderById(int orderId, int userId) {
        DonHang donHang = donHangRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với ID: " + orderId));

        if (donHang.getKhachHang() == null || donHang.getKhachHang().getMaKhachHang() != userId) {
            throw new AccessDeniedException("Bạn không có quyền truy cập đơn hàng này.");
        }

        String diaChi = donHang.getDuong() + ", " +
                donHang.getXa() + ", " +
                donHang.getHuyen() + ", " +
                donHang.getTinh();

        // Trả về thông tin của sản phẩm đầu tiên trong đơn (vì đang dùng OrderDetailResponse 1 sản phẩm)
        ChiTietDonHang chiTiet = donHang.getChiTietDonHangs().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Đơn hàng không có sản phẩm"));

        ChiTietSanPham ctsp = chiTiet.getChiTietSanPham();
        SanPham sp = ctsp.getSanPham();

        OrderDetailResponse dto = new OrderDetailResponse();
        dto.setMaDonHang(donHang.getMaDonHang());
        dto.setTenNguoiNhan(donHang.getTenNguoiNhan());
        dto.setSoDienThoai(donHang.getSoDienThoaiNguoiNhan());
        dto.setDiaChi(diaChi);
        dto.setMaSanPham(sp.getMaSanPham());
        dto.setTenSanPham(sp.getTensp());

        List<String> hinhAnhList = sp.getHinhAnh();
        dto.setHinhAnh(hinhAnhList != null && !hinhAnhList.isEmpty() ? hinhAnhList.get(0) : null);
        dto.setKichCo(ctsp.getKichCo().getTenKichCo());
        dto.setMauSac(ctsp.getMauSac().getTenMau());
        dto.setDonGia(chiTiet.getDonGia());
        dto.setSoLuong(chiTiet.getSoLuong());
        dto.setNgayTao(donHang.getNgayTao());
        dto.setNgayGiao(donHang.getNgayGiao());
        dto.setTrangThai(donHang.getTrangThai());
        dto.setCoThanhToan(donHang.isCoThanhToan());
        dto.setCoYeuCauDoiTra(donHang.isCoYeuCauDoiTra());

        return dto;
    }


}