package com.example.bankingprojectfinal.DTOS.Customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerCreateRequest {

    @NotBlank(message = "First name is mandatory")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    private String lastName;

    @Past(message = "Birth date must be a past date")
    private LocalDate birthDate;

    @NotBlank(message = "Fin code is mandatory")
    @Size(min = 7, max = 7, message = "Fin code must contain 7 characters")
    private String finCode;

    @NotBlank(message = "Phone number is mandatory")
    @Pattern(
            regexp = "^\\+994\\d{9}$",
            message = "Phone number must start with +994 and be followed by 9 digits"
    )
    private String phoneNumber;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;
}