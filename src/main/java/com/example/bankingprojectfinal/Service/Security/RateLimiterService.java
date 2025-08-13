package com.example.bankingprojectfinal.Service.Security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class RateLimiterService {

    private final StringRedisTemplate stringRedisTemplate;

    private static class WindowCounter {
        long windowStartEpochSecond;
        int count;
    }

    private final Map<String, WindowCounter> inMemoryCounters = new ConcurrentHashMap<>();

    public void ensureWithinLimit(String userKey, String actionKey, int limit, Duration window) {
        String key = "rate:" + actionKey + ":" + userKey + ":" + currentWindowKey(window);
        if (stringRedisTemplate != null) {
            Long current = stringRedisTemplate.opsForValue().increment(key);
            if (current != null && current == 1L) {
                stringRedisTemplate.expire(key, window);
            }
            if (current != null && current > limit) {
                throw new com.example.bankingprojectfinal.Exception.LimitExceedsException("Rate limit exceeded");
            }
        } else {
            WindowCounter counter = inMemoryCounters.computeIfAbsent(key, k -> {
                WindowCounter wc = new WindowCounter();
                wc.windowStartEpochSecond = Instant.now().getEpochSecond();
                wc.count = 0;
                return wc;
            });
            long now = Instant.now().getEpochSecond();
            if (now - counter.windowStartEpochSecond >= window.getSeconds()) {
                counter.windowStartEpochSecond = now;
                counter.count = 0;
            }
            counter.count++;
            if (counter.count > limit) {
                throw new com.example.bankingprojectfinal.Exception.LimitExceedsException("Rate limit exceeded");
            }
        }
    }

    private String currentWindowKey(Duration window) {
        long now = Instant.now().getEpochSecond();
        long bucket = now / window.getSeconds();
        return String.valueOf(bucket);
        
    }
}