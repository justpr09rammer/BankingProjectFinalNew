package com.example.bankingprojectfinal.DTOS.Card;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Consider adding validation annotations here, e.g., @NotBlank
// import jakarta.validation.constraints.NotBlank;

@Data // Generates getters, setters, equals, hashCode, and toString
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all fields
public class ActivateCardRequest {
    // @NotBlank(message = "Card number is required for activation") // Example validation
    private String cardNumber;
}