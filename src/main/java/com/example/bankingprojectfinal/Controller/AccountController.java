package com.example.bankingprojectfinal.Controller;


import org.springframework.web.bind.annotation.RestController;

import com.example.bankingprojectfinal.DTOS.Account.AccountCreateResponse;
import com.example.bankingprojectfinal.DTOS.Account.AccountResponse;
import com.example.bankingprojectfinal.Service.Abstraction.AccountService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
@Tag(name = "Account Management", description = "APIs for managing bank accounts (create, activate, deposit, view)")
public class AccountController {

    private final AccountService accountService;

    @Operation(summary = "Get all accounts", description = "Retrieves a paginated list of all bank accounts in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of accounts"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public Page<AccountResponse> getAllAccounts(
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0", required = false) Integer page,
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10", required = false) Integer size
    ) {
        return accountService.getAllAccounts(page, size);
    }

    @Operation(summary = "Get all active accounts", description = "Retrieves a paginated list of all accounts with ACTIVE status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of active accounts"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/allActiveAccounts")
    public Page<AccountResponse> getAllActiveAccounts(
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0", required = false) Integer page,
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10", required = false) Integer size
    ) {
        return accountService.getAllActiveAccounts(page, size);
    }

    @Operation(summary = "Get all expired accounts", description = "Retrieves a paginated list of all accounts with EXPIRED status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of expired accounts"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/allExpiredAccounts")
    public Page<AccountResponse> getAllExpiredAccounts(
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0", required = false) Integer page,
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10", required = false) Integer size
    ) {
        return accountService.getAllExpiredAccounts(page, size);
    }

    @Operation(summary = "Get accounts by customer ID", description = "Retrieves a paginated list of accounts for a specific customer, filtering by NEW and ACTIVE statuses.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of accounts for the customer"),
            @ApiResponse(responseCode = "404", description = "Customer not found (if implemented in service and handled by @ControllerAdvice)"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/accountsByCustomerId/{customerId}")
    public Page<AccountResponse> getAccountsByCustomerId(
            @Parameter(description = "ID of the customer to retrieve accounts for", required = true, example = "1")
            @PathVariable Integer customerId,
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0", required = false) Integer page,
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10", required = false) Integer size
    ) {
        return accountService.getAccountsByCustomerId(customerId, page, size);
    }

    @Operation(summary = "Create a new account", description = "Creates a new bank account for the specified customer.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Account created successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "400", description = "Maximum account limit reached for customer"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{customerId}")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountCreateResponse createAccount(
            @Parameter(description = "ID of the customer to create account for", required = true, example = "1")
            @PathVariable Integer customerId
    ) {
        return accountService.createAccount(customerId);
    }

    @Operation(summary = "Activate an account", description = "Changes the status of a NEW account to ACTIVE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Account activated successfully (No Content)"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "400", description = "Invalid account status for activation (e.g., not NEW)"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/activateAccount/{accountNumber}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activateAccount(
            @Parameter(description = "Account number to activate", required = true, example = "ACC123456789")
            @PathVariable String accountNumber
    ) {
        accountService.activateAccount(accountNumber);
    }

    @Operation(summary = "Deposit funds into an account", description = "Adds a specified amount to the balance of an ACTIVE account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Deposit successful (No Content)"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "400", description = "Invalid deposit amount or account not active"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/depositAccount/{accountNumber}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void depositAccount(
            @Parameter(description = "Account number to deposit into", required = true, example = "ACC123456789")
            @PathVariable String accountNumber,
            @Parameter(description = "Amount to deposit", required = true, example = "100.50")
            @RequestParam BigDecimal amount
    ) {
        accountService.depositAccount(accountNumber, amount);
    }
}