package com.example.bankingprojectfinal.Service.Abstraction;

import com.example.bankingprojectfinal.DTOS.Card.*;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

public interface CardService {
    CardCreateResponse createCard(CreateCardRequest cardRequest);
    ActivateCardResponse activateCard(ActivateCardRequest request);
    DepositCardResponse depositCard(DepositCardRequest request);
    Page<CardDto> getCardsByAccount(String accountNumber,Integer page, Integer size);

    Page<CardDto> getCardsByCustomerId(Integer customerId, Integer page, Integer size);

    Page<CardDto> getAllActiveCards(Integer page, Integer size);
    Page<CardDto> getAllCards(Integer page, Integer size);

}