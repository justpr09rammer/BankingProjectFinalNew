package com.example.bankingprojectfinal.Utils;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class AccountNumberGenerator {
    public String generate() {
        return "ACC" + Instant.now().getEpochSecond() +
                ThreadLocalRandom.current().nextInt(100, 999);
    }
}