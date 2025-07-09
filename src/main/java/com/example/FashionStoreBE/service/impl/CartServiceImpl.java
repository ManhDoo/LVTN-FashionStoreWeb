package com.example.FashionStoreBE.service.impl;

import com.example.FashionStoreBE.exception.ApiException;
import com.example.FashionStoreBE.model.GioHang;
import com.example.FashionStoreBE.repository.ProductDetailRopository;
import com.example.FashionStoreBE.service.CartService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final ProductDetailRopository productDetailRopository;

    public CartServiceImpl(StringRedisTemplate redisTemplate, ObjectMapper objectMapper, ProductDetailRopository productDetailRopository) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.productDetailRopository = productDetailRopository;
    }

    private String getKey(int userId) {
        return "cart:user:" + userId;
    }


    @Override
    public void addToCart(int userId, GioHang item) {
        boolean existProductDetail = productDetailRopository.existsById(item.getProductId());
        if (!existProductDetail) {
            throw new ApiException("Không thể thêm vào giỏ hàng: Sản phẩm không tồn tại");
        }
        String key = getKey(userId);
        List<GioHang> cart = getCart(userId);

        Optional<GioHang> existingItem = cart.stream()
                .filter(i -> i.getProductId() == item.getProductId())
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + item.getQuantity());
        } else {
            cart.add(item);
        }

        try {
            String updatedCart = objectMapper.writeValueAsString(cart);
            redisTemplate.opsForValue().set(key, updatedCart);
        } catch (Exception e) {
            throw new RuntimeException("Không thể lưu giỏ hàng", e);
        }
    }

    @Override
    public void removeFromCart(int userId, int productId) {
        List<GioHang> cart = getCart(userId);
        boolean removed = cart.removeIf(item -> item.getProductId() == productId);

        if (!removed) {
            throw new ApiException("Không thể xóa: Sản phẩm không có trong giỏ hàng");
        }

        try {
            redisTemplate.opsForValue().set(getKey(userId), objectMapper.writeValueAsString(cart));
        } catch (Exception e) {
            throw new ApiException("Không thể cập nhật giỏ hàng");
        }
    }

    @Override
    public List<GioHang> getCart(int userId) {
        String json = redisTemplate.opsForValue().get(getKey(userId));
        if (json == null) return new ArrayList<>();

        try {
            return objectMapper.readValue(json, new TypeReference<List<GioHang>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public void clearCart(int userId) {
        redisTemplate.delete(getKey(userId));
    }

    @Override
    public void updateQuantity(int userId, int productId, int newQuantity) {
        if (newQuantity <= 0) {
            throw new ApiException("Số lượng phải lớn hơn 0");
        }

        List<GioHang> cart = getCart(userId);
        boolean found = false;

        for (GioHang item : cart) {
            if (item.getProductId() == productId) {
                item.setQuantity(newQuantity);
                found = true;
                break;
            }
        }

        if (!found) {
            throw new ApiException("Sản phẩm không tồn tại trong giỏ hàng");
        }

        try {
            String updatedCart = objectMapper.writeValueAsString(cart);
            redisTemplate.opsForValue().set(getKey(userId), updatedCart);
        } catch (Exception e) {
            throw new ApiException("Không thể cập nhật giỏ hàng");
        }
    }

}
