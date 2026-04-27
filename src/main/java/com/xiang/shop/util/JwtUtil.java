package com.xiang.shop.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private static final SecretKey SECRET_KEY = Jwts.SIG.HS256.key().build();

    // 設定有效期限 30 分鐘
    private static final long EXPIRATION_TIME = 1800000;

    // 產生 Token
    public String generateToken(String account, String role) {

        return Jwts.builder()
                .subject(account)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 到期時間
                .signWith(SECRET_KEY)
                .compact();

    }

    // 透過 Token 取得帳號
    public String getAccountFromToken(String token) {

        Claims claims = Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();

    }

    // 透過 Token 取得角色
    public String getRoleFromToken(String token) {

        Claims claims = Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("role", String.class);

    }

    // 驗證 Token
    public boolean validateToken(String token) {

        try {
            Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

}