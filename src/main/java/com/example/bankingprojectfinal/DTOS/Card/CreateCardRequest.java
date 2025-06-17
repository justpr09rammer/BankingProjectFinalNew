package com.example.bankingprojectfinal.DTOS.Card;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class CreateCardRequest {
    @NotBlank(message = "Account number is required")
    private String accountNumber;
}
