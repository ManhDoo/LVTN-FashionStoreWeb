package com.example.FashionStoreBE.config;

import com.example.FashionStoreBE.model.ChiTietSanPham;
import com.example.FashionStoreBE.repository.ProductDetailRopository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TonKhoInitializer implements ApplicationRunner {

    private final ProductDetailRopository chiTietSanPhamRepo;
    private final RedisTemplate<String, Integer> redisTemplate;

    @Override
    public void run(ApplicationArguments args) {
        List<ChiTietSanPham> list = chiTietSanPhamRepo.findAll();
        for (ChiTietSanPham ct : list) {
            String key = "TON_KHO:" + ct.getId();
            redisTemplate.opsForValue().set(key, ct.getTonKho());
        }
        System.out.println("✅ Đã khởi tạo tồn kho vào Redis");
    }
}
