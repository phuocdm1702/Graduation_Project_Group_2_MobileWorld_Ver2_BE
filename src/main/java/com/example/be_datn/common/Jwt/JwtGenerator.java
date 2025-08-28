package com.example.be_datn.common.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtGenerator {
    public static void main(String[] args) {
        // Secret key (phải giữ bí mật, không để lộ)
        String secretKey = "your-secure-secret-key-1234567890abcdef"; // Thay bằng key của bạn

        // Payload (dữ liệu mã hóa trong token)
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", "12345");
        claims.put("role", "delivery_api");
        claims.put("service", "delivery_service");

        // Thời gian hết hạn (1 giờ từ hiện tại)
        long expirationTime = 1000 * 60 * 60; // 1 giờ (tính bằng mili giây)
        Date issuedAt = new Date();
        Date expiration = new Date(issuedAt.getTime() + expirationTime);

        // Tạo JWT token
        String jwt = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();

        System.out.println("JWT Token: " + jwt);
    }
}