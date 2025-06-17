package com.example.bankingprojectfinal.Controller;

import com.example.bankingprojectfinal.DTOS.Transaction.TransactionDto;
import com.example.bankingprojectfinal.DTOS.Transaction.TransferRequest; // Import the new DTO
import com.example.bankingprojectfinal.Service.Abstraction.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Add SLF4J for logging
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Slf4j // Enable logging
@Tag(name = "Transaction Management", description = "APIs for initiating and viewing bank transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Operation(summary = "Initiate a new bank transfer",
               description = "Performs an account-to-account transfer. The actual processing might be asynchronous based on system configuration (e.g., via a scheduled job for PENDING transactions).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transaction initiated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data (e.g., wrong account format, amount <= 0) or business rule violation (e.g., insufficient funds, account not active, daily limit exceeded)"),
            @ApiResponse(responseCode = "404", description = "Account not found"), // If an account ID doesn't exist
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/transfer")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionDto initiateTransfer(
            @Parameter(description = "Details for the transfer, including debit and credit account numbers and amount", required = true)
            @Valid @RequestBody TransferRequest request
    ) {
        log.info("Received transfer request: Debit={}, Credit={}, Amount={}",
                request.getDebitAccountNumber(), request.getCreditAccountNumber(), request.getAmount());
        return transactionService.transfer(
                request.getDebitAccountNumber(),
                request.getCreditAccountNumber(),
                request.getAmount()
        );
    }

    @Operation(summary = "Get transactions by customer ID",
               description = "Retrieves a paginated list of transactions associated with a specific customer.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of transactions for the customer"),
            @ApiResponse(responseCode = "400", description = "Invalid customer ID format (if path variable validation is added)"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/byCustomer/{customerId}")
    public Page<TransactionDto> getTransactionsByCustomerId(
            @Parameter(description = "ID of the customer to retrieve transactions for", required = true, example = "101")
            @PathVariable Integer customerId,
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0", required = false) Integer page,
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10", required = false) Integer size
    ) {
        log.info("Fetching transactions for customer ID: {} (Page: {}, Size: {})", customerId, page, size);
        return transactionService.getTransactionsByCustomerId(customerId, page, size);
    }

    @Operation(summary = "Get all transactions",
               description = "Retrieves a paginated list of all bank transactions in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of all transactions"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public Page<TransactionDto> getAllTransactions(
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0", required = false) Integer page,
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10", required = false) Integer size
    ) {
        log.info("Fetching all transactions (Page: {}, Size: {})", page, size);
        return transactionService.getAllTransactions(page, size);
    }
}