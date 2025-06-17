package com.example.bankingprojectfinal.DTOS.Transaction;// package com.example.bankingprojectfinal.DTOS.Transaction;
// You might want to put this in a dedicated request DTO package, e.g.,
// package com.example.bankingprojectfinal.DTOS.Transaction.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO for initiating a bank transfer")
public class TransferRequest {

    @NotBlank(message = "Debit account number cannot be empty")
    @Pattern(regexp = "^\\d{20}$", message = "Debit account number must be 20 digits")
    @Schema(description = "The 20-digit account number from which funds will be debited", example = "12345678901234567890")
    private String debitAccountNumber;

    @NotBlank(message = "Credit account number cannot be empty")
    @Pattern(regexp = "^\\d{20}$", message = "Credit account number must be 20 digits")
    @Schema(description = "The 20-digit account number to which funds will be credited", example = "09876543210987654321")
    private String creditAccountNumber;

    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.01", inclusive = true, message = "Amount must be greater than zero")
    @Schema(description = "The amount to transfer", example = "100.50")
    private BigDecimal amount;
}