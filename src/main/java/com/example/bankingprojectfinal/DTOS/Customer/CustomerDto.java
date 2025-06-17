package com.example.bankingprojectfinal.DTOS.Customer;

import com.example.bankingprojectfinal.Model.Enums.CustomerStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Integer customerId;

    @NotBlank(message = "First name is mandatory")
    String firstName;

    @NotBlank(message = "Last name is mandatory")
    String lastName;

    @Past(message = "Birth date must be past date")
    LocalDate birthDate;

    @NotBlank(message = "Fin code is mandatory")
    @Size(min = 7, max = 7, message = "Fin code size should be contained 7 characters")
    String finCode;

    @NotBlank(message = "Phone number is mandatory")
    @Pattern(
            regexp = "^\\+994\\d{9}$",
            message = "Phone number must start with +994 and be followed by 9 digits"
    )
    String phoneNumber;

    @NotBlank(message = "First name is mandatory")
    @Email(message = "Email should be valid")
    String email;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    LocalDate registrationDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    CustomerStatus status;
}
