package com.studyconnetct.authservice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class JwtUtil {
    
    @Value("${jwt.secret:mySecretKeyForJWTTokenGenerationAndValidationPurposeOnly12345}")
    private String jwtSecret;
    
    @Value("${jwt.expiration:86400000}") // 24 hours in milliseconds (default)
    private Long jwtExpiration;
    
    @Value("${jwt.refresh-expiration:86400000}") // 24 hours in milliseconds
    private Long refreshExpiration;
    
    private SecretKey key;
    
    public SecretKey getKey() {
        if (key == null) {
            key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        }
        return key;
    }
    
    public String generateAccessToken(UUID userId, String email, java.util.Set<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("roles", roles);
        return createToken(claims, userId.toString(), jwtExpiration);
    }
    
    public String generateRefreshToken(UUID userId) {
        return createToken(new HashMap<>(), userId.toString(), refreshExpiration);
    }
    
    private String createToken(Map<String, Object> claims, String subject, Long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    public UUID getUserIdFromToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return UUID.fromString(claims.getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Failed to get user ID from token", e);
            return null;
        }
    }
    
    public String getEmailFromToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return (String) claims.get("email");
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Failed to get email from token", e);
            return null;
        }
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT validation failed", e);
            return false;
        }
    }
    
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Failed to check token expiration", e);
            return true;
        }
    }
    
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    public Long getTokenExpirationTime() {
        return jwtExpiration;
    }
}
