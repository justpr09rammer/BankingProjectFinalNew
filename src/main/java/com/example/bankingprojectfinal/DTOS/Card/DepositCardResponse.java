package com.example.bankingprojectfinal.DTOS.Card; // Or a more general 'DTOS.Transaction' package

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepositCardResponse {
    private boolean success;
    private String message;
    private String cardNumber; // Confirm the card used
    private BigDecimal depositedAmount; // Confirm the amount deposited
    private BigDecimal newAccountBalance; // **Crucial feedback for the user**
    private String transactionId; // A unique ID for this deposit transaction
    private LocalDateTime transactionTimestamp; // When the deposit occurred
}