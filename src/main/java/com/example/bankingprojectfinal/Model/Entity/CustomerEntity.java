package com.example.bankingprojectfinal.Model.Entity;


import com.example.bankingprojectfinal.Model.Enums.CustomerStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
@Builder
@Entity
@Table(name = "customer")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String finCode;
    private String phoneNumber;
    private String email;
    private LocalDate registrationDate;

    @Enumerated(EnumType.STRING)
    private CustomerStatus status;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccountEntity> accountList;
}