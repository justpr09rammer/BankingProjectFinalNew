package com.example.bankingprojectfinal.DTOS.User;

import com.example.bankingprojectfinal.Model.Enums.UserStatus;
import com.example.bankingprojectfinal.Model.Enums.UserRole;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for returning full user details after creation, update, or retrieval.
 * Does not expose sensitive information like raw password.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String username;
    UserRole role;
    UserStatus status;
    LocalDate registrationDate;
}