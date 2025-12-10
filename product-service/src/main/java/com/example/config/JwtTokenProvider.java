package com.example.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT Token Provider - Xử lý tạo, xác thực, và giải mã JWT tokens
 */
@Component
public class JwtTokenProvider {
    
    @Value("${jwt.secret:mySecretKeyThatIsLongEnoughFor256BitHmacSha256AlgorithmAndCannotBeGuessed123456789}")
    private String jwtSecret;
    
    @Value("${jwt.expiration:86400000}")  // 24 hours
    private Long jwtExpirationMs;
    
    /**
     * Tạo JWT token từ user ID và username
     */
    public String generateToken(Integer userId, String userName) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
        
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("userName", userName)
                .claim("userId", userId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), io.jsonwebtoken.SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * Lấy user ID từ token
     */
    public Integer getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            Object userIdObj = claims.get("userId");
            if (userIdObj instanceof Integer) {
                return (Integer) userIdObj;
            }
            return Integer.parseInt(userIdObj.toString());
        } catch (JwtException | NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * Lấy username từ token
     */
    public String getUserNameFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("userName", String.class);
        } catch (JwtException e) {
            return null;
        }
    }
    
    /**
     * Xác thực token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * Kiểm tra token hết hạn
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }
    
    /**
     * Lấy signing key từ secret string
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
}
