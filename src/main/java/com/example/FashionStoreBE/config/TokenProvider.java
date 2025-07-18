package com.example.FashionStoreBE.config;


import com.example.FashionStoreBE.exception.UserException;
import com.example.FashionStoreBE.model.KhachHang;
import com.example.FashionStoreBE.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class TokenProvider {
    SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());

    @Autowired
    private UserRepository userRepository;

    public String generateToken(KhachHang user){
        String role = "ROLE_" + user.getQuyen();

        String jwt = Jwts.builder().setIssuer("Code with Manh")
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + 7 * 24 * 60 * 60 * 1000))
                .claim("email", user.getEmail())
                .claim("id", user.getMaKhachHang())       // ✅ thêm id
                .claim("role", role)           // ✅ thêm role
                .claim("authorities", role)    // ✅ cần cho filter
                .signWith(key)
                .compact();
        return jwt;
    }

    public String getEmailFromToken(String jwt){
        jwt = jwt.substring(7);
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();

        String email = String.valueOf(claims.get("email"));
        return email;
    }

    public int getUserIdFromToken(String jwt) {
        if (jwt == null || jwt.trim().isEmpty()) {
            throw new IllegalArgumentException("Token JWT không hợp lệ hoặc rỗng.");
        }
        if (jwt.startsWith("Bearer ")) {
            jwt = jwt.substring(7);
        }
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();
        return Integer.parseInt(String.valueOf(claims.get("id")));
    }

}
