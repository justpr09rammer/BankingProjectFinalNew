package com.example.bankingprojectfinal.DTOS.Card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivateCardResponse {
    private boolean success;
    private String message;
    private CardDto card;
    private LocalDate previousExpireDate;
}