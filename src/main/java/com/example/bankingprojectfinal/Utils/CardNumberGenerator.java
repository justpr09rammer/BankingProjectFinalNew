package com.example.bankingprojectfinal.Utils;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class CardNumberGenerator {





    public String generate() {
        return "CARD" + Instant.now().getEpochSecond() +
                String.format("%04d", ThreadLocalRandom.current().nextInt(1, 10000)); // 4-digit random padding
    }
}