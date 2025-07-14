package com.example.FashionStoreBE.controller;

import com.example.FashionStoreBE.config.TokenProvider;
import com.example.FashionStoreBE.dto.request.GuestOrderRequest;
import com.example.FashionStoreBE.dto.response.OrderDetailResponse;
import com.example.FashionStoreBE.model.DonHang;
import com.example.FashionStoreBE.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private TokenProvider tokenProvider;

    private int extractUserIdFromRequest(HttpServletRequest request) {
        String jwt = request.getHeader("Authorization");
        return tokenProvider.getUserIdFromToken(jwt);
    }

    @PostMapping("/place")
    public ResponseEntity<String> placeOrder(HttpServletRequest request) {
        int userId = extractUserIdFromRequest(request);
        String result = orderService.placeOrder(userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/guest/place")
    public ResponseEntity<String> placeOrderForGuest(@RequestBody GuestOrderRequest request) throws Exception {
        String result = orderService.placeOrderForGuest(request);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<Page<OrderDetailResponse>> getOrdersByUser(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        int userId = extractUserIdFromRequest(request);
        Page<OrderDetailResponse> orders = orderService.getOrdersByUserId(userId, page, size);
        return ResponseEntity.ok(orders);
    }


    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<String> cancelOrder(HttpServletRequest request, @PathVariable int orderId) {
        int userId = extractUserIdFromRequest(request);
        String result = orderService.cancelOrder(userId, orderId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OrderDetailResponse>> getAllOrders(@RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "10") int size) {
        Page<OrderDetailResponse> orders = orderService.getAllOrders(page, size);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateOrderStatus(
            @PathVariable int orderId,
            @RequestParam String status) {
        orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok("Cập nhật trạng thái đơn hàng thành công.");
    }

    @GetMapping("/filter")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OrderDetailResponse>> getOrders(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<OrderDetailResponse> response = orderService.getOrdersByTrangThai(status, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("{orderId}")
    public ResponseEntity<OrderDetailResponse> getOrderDetail(@PathVariable int orderId, HttpServletRequest request) {
        int userId = extractUserIdFromRequest(request);
        OrderDetailResponse response = orderService.getOrderById(orderId, userId);
        return ResponseEntity.ok(response);
    }

}
