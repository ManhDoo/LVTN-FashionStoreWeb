package com.example.FashionStoreBE.service;

import com.example.FashionStoreBE.model.GioHang;

import java.util.List;

public interface CartService {
    void addToCart(int userId, GioHang item);
    void removeFromCart(int userId, int productId);
    List<GioHang> getCart(int userId);
    void clearCart(int userId);
    void updateQuantity(int userId, int productId, int newQuantity);

}
