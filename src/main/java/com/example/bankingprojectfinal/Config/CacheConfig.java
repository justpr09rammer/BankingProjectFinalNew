package com.example.bankingprojectfinal.Config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    @ConditionalOnProperty(name = "spring.data.redis.host", matchIfMissing = true)
    public CacheManager cacheManager() {
        // Simple in-memory cache by default; Redis config can be added when host provided
        return new ConcurrentMapCacheManager("balances", "rateLimits", "jwtBlacklist");
    }
}