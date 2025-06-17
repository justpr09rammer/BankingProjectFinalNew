package com.example.bankingprojectfinal.DTOS.Card;

import com.example.bankingprojectfinal.Model.Enums.CardStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

@FieldDefaults(level = AccessLevel.PRIVATE)
public class CardDto {
    String accountNumber;
    String cardNumber;
    BigDecimal balance;
    LocalDate issueDate;
    LocalDate expireDate;
    CardStatus status;
}
