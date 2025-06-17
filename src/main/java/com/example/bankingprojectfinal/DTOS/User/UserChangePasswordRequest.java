package com.example.bankingprojectfinal.DTOS.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Request DTO for changing a user's password.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserChangePasswordRequest {
    @NotBlank(message = "Username cannot be blank")
    String username;

    @NotBlank(message = "Old password cannot be blank")
    String oldPassword;

    @NotBlank(message = "New password cannot be blank")
    @Size(min = 8, message = "New password must be at least 8 characters long")
    String newPassword;
}