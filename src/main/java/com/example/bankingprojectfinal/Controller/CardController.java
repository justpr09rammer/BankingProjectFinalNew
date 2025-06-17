package com.example.bankingprojectfinal.Controller;

import com.example.bankingprojectfinal.DTOS.Card.*;
import com.example.bankingprojectfinal.Service.Abstraction.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; // For @Valid annotation
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
@Tag(name = "Card Management", description = "APIs for managing bank cards (create, activate, deposit, view)")
public class CardController {

    private final CardService cardService;

    @Operation(summary = "Create a new card", description = "Creates a new bank card for a specified account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Card created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data or business rule violation (e.g., max cards reached, invalid account status)"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CardCreateResponse createCard(
            @Parameter(description = "Details for card creation, including account number", required = true)
            @Valid @RequestBody CreateCardRequest cardRequest
    ) {
        return cardService.createCard(cardRequest);
    }

    @Operation(summary = "Activate a card", description = "Changes the status of a NEW card to ACTIVE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card activated successfully"),
            @ApiResponse(responseCode = "404", description = "Card not found"),
            @ApiResponse(responseCode = "400", description = "Invalid card status for activation (e.g., not NEW)"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/activate")
    public ActivateCardResponse activateCard(
            @Parameter(description = "Request to activate a card by card number", required = true)
            @Valid @RequestBody ActivateCardRequest request
    ) {
        return cardService.activateCard(request);
    }

    @Operation(summary = "Deposit funds to an account via card", description = "Adds a specified amount to the balance of the account linked to an ACTIVE card.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deposit successful"),
            @ApiResponse(responseCode = "404", description = "Card or linked account not found"),
            @ApiResponse(responseCode = "400", description = "Invalid deposit amount or card/account not active"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/deposit")
    public DepositCardResponse depositCard(
            @Parameter(description = "Request to deposit funds, including card number and amount", required = true)
            @Valid @RequestBody DepositCardRequest request
    ) {
        return cardService.depositCard(request);
    }

    @Operation(summary = "Get cards by account number", description = "Retrieves a paginated list of cards associated with a specific account number.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of cards for the account"),
            @ApiResponse(responseCode = "404", description = "Account not found (if service throws exception)"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/byAccount/{accountNumber}")
    public Page<CardDto> getCardsByAccount(
            @Parameter(description = "Account number to retrieve cards for", required = true, example = "ACC123456789")
            @PathVariable String accountNumber,
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0", required = false) Integer page,
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10", required = false) Integer size
    ) {
        return cardService.getCardsByAccount(accountNumber, page, size);
    }

    @Operation(summary = "Get cards by customer ID", description = "Retrieves a paginated list of cards associated with accounts owned by a specific customer ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of cards for the customer"),
            @ApiResponse(responseCode = "404", description = "Customer not found (if implemented in service and handled by @ControllerAdvice)"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/byCustomer/{customerId}")
    public Page<CardDto> getCardsByCustomerId(
            @Parameter(description = "ID of the customer to retrieve cards for", required = true, example = "1")
            @PathVariable Integer customerId,
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0", required = false) Integer page,
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10", required = false) Integer size
    ) {
        return cardService.getCardsByCustomerId(customerId, page, size);
    }

    @Operation(summary = "Get all active cards", description = "Retrieves a paginated list of all cards with ACTIVE status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of active cards"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/active")
    public Page<CardDto> getAllActiveCards(
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0", required = false) Integer page,
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10", required = false) Integer size
    ) {
        return cardService.getAllActiveCards(page, size);
    }

    @Operation(summary = "Get all cards", description = "Retrieves a paginated list of all bank cards in the system, regardless of status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of all cards"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public Page<CardDto> getAllCards(
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0", required = false) Integer page,
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10", required = false) Integer size
    ) {
        return cardService.getAllCards(page, size);
    }
}