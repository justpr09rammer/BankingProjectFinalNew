package com.example.bankingprojectfinal.Service.Security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class JwtBlacklistService {

    private final StringRedisTemplate stringRedisTemplate;

    private final Map<String, Long> inMemoryBlacklist = new ConcurrentHashMap<>();

    public void blacklist(String jti, long ttlSeconds) {
        Objects.requireNonNull(jti, "jti");
        if (stringRedisTemplate != null) {
            stringRedisTemplate.opsForValue().set(redisKey(jti), "1", ttlSeconds, TimeUnit.SECONDS);
        } else {
            inMemoryBlacklist.put(jti, Instant.now().getEpochSecond() + ttlSeconds);
        }
    }

    public boolean isBlacklisted(String jti) {
        if (jti == null) return false;
        if (stringRedisTemplate != null) {
            Boolean exists = stringRedisTemplate.hasKey(redisKey(jti));
            return exists != null && exists;
        }
        Long exp = inMemoryBlacklist.get(jti);
        if (exp == null) return false;
        if (exp < Instant.now().getEpochSecond()) {
            inMemoryBlacklist.remove(jti);
            return false;
        }
        return true;
    }

    private String redisKey(String jti) {
        return "jwt:blacklist:" + jti;
    }
}