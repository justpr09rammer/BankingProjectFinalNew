package com.example.bankingprojectfinal.DTOS.Card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardCreateResponse {
    private boolean success;
    private String message;
    private CardDto card;
}