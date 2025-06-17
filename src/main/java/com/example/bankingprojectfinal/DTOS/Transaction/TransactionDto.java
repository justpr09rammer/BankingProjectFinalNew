package com.example.bankingprojectfinal.DTOS.Transaction;

import com.example.bankingprojectfinal.Model.Enums.TransactionStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class TransactionDto {
    String transactionId;
    Integer customerId;
    String debit;
    String credit;
    LocalDate transactionDate;
    BigDecimal amount;
    TransactionStatus status;
}
