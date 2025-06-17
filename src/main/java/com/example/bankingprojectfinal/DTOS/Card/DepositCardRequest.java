package com.example.bankingprojectfinal.DTOS.Card; // Or a more general 'DTOS.Transaction' package

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepositCardRequest {
    @NotBlank(message = "Card number cannot be blank")
    private String cardNumber;

    @NotNull(message = "Deposit amount cannot be null")
    @DecimalMin(value = "0.01", message = "Deposit amount must be positive")
    private BigDecimal amount;
}