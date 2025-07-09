package com.example.FashionStoreBE.controller;

import com.example.FashionStoreBE.config.TokenProvider;
import com.example.FashionStoreBE.model.GioHang;
import com.example.FashionStoreBE.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    private CartService cartService;

    @Autowired
    private TokenProvider tokenProvider;


    // Lấy id từ JWT token
    private int extractUserIdFromRequest(HttpServletRequest request) {
        String jwt = request.getHeader("Authorization");
        return tokenProvider.getUserIdFromToken(jwt);
    }

    // Thêm sản phẩm vào giỏ hàng
    @PostMapping("/add")
    public ResponseEntity<String> addToCart(@RequestBody GioHang item, HttpServletRequest request) {
        int userId = extractUserIdFromRequest(request);
        cartService.addToCart(userId, item);
        return ResponseEntity.ok("Đã thêm vào giỏ hàng");
    }

    // Xóa sản phẩm khỏi giỏ hàng
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<String> removeFromCart(@PathVariable int productId, HttpServletRequest request) {
        int userId = extractUserIdFromRequest(request);
        cartService.removeFromCart(userId, productId);
        return ResponseEntity.ok("Đã xóa sản phẩm khỏi giỏ hàng");
    }

    // Lấy toàn bộ giỏ hàng
    @GetMapping
    public ResponseEntity<List<GioHang>> getCart(HttpServletRequest request) {
        int userId = extractUserIdFromRequest(request);
        List<GioHang> cart = cartService.getCart(userId);
        return ResponseEntity.ok(cart);
    }

    // Xóa toàn bộ giỏ hàng
    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart(HttpServletRequest request) {
        int userId = extractUserIdFromRequest(request);
        cartService.clearCart(userId);
        return ResponseEntity.ok("Đã xóa toàn bộ giỏ hàng");
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateQuantity(@RequestParam int productId,
                                                 @RequestParam int quantity,
                                                 HttpServletRequest request) {
        int userId = extractUserIdFromRequest(request);
        cartService.updateQuantity(userId, productId, quantity);
        return ResponseEntity.ok("Đã cập nhật số lượng sản phẩm");
    }

}
