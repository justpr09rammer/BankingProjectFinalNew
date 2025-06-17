package com.example.bankingprojectfinal.DTOS.Customer;

import com.example.bankingprojectfinal.Model.Enums.CustomerStatus;
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
public class CustomerResponse {
    private Integer customerId;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String finCode;
    private String phoneNumber;
    private String email;
    private LocalDate registrationDate;
    private CustomerStatus status;
}