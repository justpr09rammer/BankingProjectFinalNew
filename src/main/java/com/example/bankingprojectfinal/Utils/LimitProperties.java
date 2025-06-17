package com.example.bankingprojectfinal.Utils;

import lombok.Data;
import lombok.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
@Component
@Data
public class LimitProperties {
    private final Integer maxAccountCountPerCustomer = 3;

    private final Integer maxCardCountPerAccount = 2;

    private final BigDecimal dailyTransactionLimit = BigDecimal.valueOf(1000.00);

    private final BigDecimal minAcceptableAccountBalance = BigDecimal.valueOf(50);

    private final BigDecimal minAcceptableCardBalance = BigDecimal.valueOf(0.00);
    private final BigDecimal monthlyTransactionSuspectLimit = BigDecimal.valueOf(10000.00);
    private final BigDecimal monthlyTransactionBlockedLimit = BigDecimal.valueOf(100000.00);




}
