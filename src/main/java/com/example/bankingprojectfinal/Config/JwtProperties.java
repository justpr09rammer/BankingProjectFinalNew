package com.example.bankingprojectfinal.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProperties {

    @Value("${app.jwt.secret:change-me-super-secret-key-change-me-change-me-0123456789}")
    private String secret;

    @Value("${app.jwt.access-ttl-seconds:900}")
    private long accessTtlSeconds;

    @Value("${app.jwt.refresh-ttl-seconds:604800}")
    private long refreshTtlSeconds;

    public String getSecret() {
        return secret;
    }

    public long getAccessTtlSeconds() {
        return accessTtlSeconds;
    }

    public long getRefreshTtlSeconds() {
        return refreshTtlSeconds;
    }
}