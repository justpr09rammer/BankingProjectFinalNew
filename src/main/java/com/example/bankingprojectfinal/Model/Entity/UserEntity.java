package com.example.bankingprojectfinal.Model.Entity;

import com.example.bankingprojectfinal.Model.Enums.UserRole;
import com.example.bankingprojectfinal.Model.Enums.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Table(name = "app_user")
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
    @Id
    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private LocalDate registrationDate;

    @Enumerated(EnumType.STRING)
    private UserStatus status;
}