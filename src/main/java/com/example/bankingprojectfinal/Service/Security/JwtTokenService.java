package com.example.bankingprojectfinal.Service.Security;

import com.example.bankingprojectfinal.Config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
public class JwtTokenService {

    private final JwtProperties jwtProperties;
    private final SecretKey key;

    public JwtTokenService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(String username, String role) {
        return generateToken(username, role, jwtProperties.getAccessTtlSeconds(), "access");
    }

    public String generateRefreshToken(String username, String role) {
        return generateToken(username, role, jwtProperties.getRefreshTtlSeconds(), "refresh");
    }

    private String generateToken(String username, String role, long ttlSeconds, String type) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(ttlSeconds);
        String jti = UUID.randomUUID().toString();
        return Jwts.builder()
                .id(jti)
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .claims(Map.of("role", role, "type", type))
                .signWith(key)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isRefreshToken(String token) {
        return "refresh".equalsIgnoreCase((String) parseClaims(token).get("type"));
    }

    public boolean isAccessToken(String token) {
        return "access".equalsIgnoreCase((String) parseClaims(token).get("type"));
    }

    public String getUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public String getRole(String token) {
        Object role = parseClaims(token).get("role");
        return role == null ? null : role.toString();
    }

    public String getJti(String token) {
        return parseClaims(token).getId();
    }

    public long getSecondsUntilExpiration(String token) {
        Date exp = parseClaims(token).getExpiration();
        long seconds = (exp.getTime() - System.currentTimeMillis()) / 1000L;
        return Math.max(0, seconds);
    }
}