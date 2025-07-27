package com.example.be_datn.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secretKey;

    private final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hour

    private Key getSigningKey() {
        if (secretKey == null || secretKey.trim().isEmpty()) {
            throw new IllegalArgumentException("Khóa bí mật JWT không được để trống trong application.properties");
        }
        try {
            byte[] keyBytes = Base64.getDecoder().decode(secretKey);
            if (keyBytes.length < 64) {
                throw new IllegalArgumentException("Khóa bí mật JWT có kích thước " + (keyBytes.length * 8) + " bit, phải ít nhất 512 bit (64 byte)");
            }
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Khóa bí mật JWT không phải là chuỗi Base64 hợp lệ", e);
        }
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenExpired(String token) {
        return extractAllClaims(token)
                .getExpiration()
                .before(new Date());
    }

    public boolean validateToken(String token, String username) {
        return username.equals(extractUsername(token)) && !isTokenExpired(token);
    }

}
