package com.example.FashionStoreBE.service;

import com.example.FashionStoreBE.dto.request.GuestOrderRequest;
import com.example.FashionStoreBE.dto.request.PhieuTraHangRequest;
import com.example.FashionStoreBE.dto.response.OrderDetailResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {
    String placeOrder(int userId);
    String placeOrderForGuest(GuestOrderRequest request) throws Exception;
    List<OrderDetailResponse> getOrdersByUserId(int userId);
    String cancelOrder(int userId, int orderId);
    Page<OrderDetailResponse> getAllOrders(int page, int size);
    void updateOrderStatus(int orderId, String newStatus);
    Page<OrderDetailResponse> getOrdersByTrangThai(String trangThai, int page, int size);

}
