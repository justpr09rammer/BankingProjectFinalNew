package com.example.bankingprojectfinal.DTOS.Account;

import com.example.bankingprojectfinal.Model.Enums.AccountStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public class AccountResponse {
    String accountNumber;
    Integer customerId;
    BigDecimal balance;
    LocalDate openingDate;
    LocalDate expireDate;
    AccountStatus status;
    // Note: 'success' and 'message' are typically for creation/update responses,
    // not general read responses. So, I've kept this as a standard 'read' DTO.
}