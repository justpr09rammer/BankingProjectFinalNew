package com.example.bankingprojectfinal.DTOS.Account;
import com.example.bankingprojectfinal.Model.Enums.AccountStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountCreateResponse {
    String accountNumber;
    Integer customerId;
    BigDecimal balance;
    LocalDate openingDate;
    LocalDate expireDate;
    AccountStatus status;
    boolean success;
    String message;
}