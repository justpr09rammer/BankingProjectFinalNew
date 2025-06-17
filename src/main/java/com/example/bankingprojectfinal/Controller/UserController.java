package com.example.bankingprojectfinal.Controller;

// Request/Response DTOs
import com.example.bankingprojectfinal.DTOS.User.PasswordChangeResponse;
import com.example.bankingprojectfinal.DTOS.User.UserChangePasswordRequest;
import com.example.bankingprojectfinal.DTOS.User.UserCreateRequest;
import com.example.bankingprojectfinal.DTOS.User.UserResponse;
import com.example.bankingprojectfinal.DTOS.User.UserStatusChangeRequest;

// Service Abstraction
import com.example.bankingprojectfinal.Service.Abstraction.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "User Management", description = "APIs for managing application users (create, activate, disable, change password, view all)")
@Slf4j
public class UserController {

    UserService userService;

    @Operation(summary = "Create a new generic user",
            description = "Registers a new standard user in the system. Requires a unique username.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Generic user created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input (e.g., validation errors)"),
            @ApiResponse(responseCode = "409", description = "Conflict: User with provided username already exists")
    })
    @PostMapping("/generic")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createGenericUser(
            @Parameter(description = "User details (username, password) for generic user creation", required = true)
            @Valid @RequestBody UserCreateRequest request
    ) {
        log.info("Received request to create generic user: {}", request.getUsername());
        return userService.createGenericUser(request);
    }

    @Operation(summary = "Create a new admin user",
            description = "Registers a new administrative user in the system. Requires a unique username.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Admin user created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input (e.g., validation errors)"),
            @ApiResponse(responseCode = "409", description = "Conflict: User with provided username already exists")
    })
    @PostMapping("/admin")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createAdminUser(
            @Parameter(description = "User details (username, password) for admin user creation", required = true)
            @Valid @RequestBody UserCreateRequest request
    ) {
        log.info("Received request to create admin user: {}", request.getUsername());
        return userService.createAdminUser(request);
    }

    @Operation(summary = "Activate an existing user",
            description = "Sets the status of a specified user to ACTIVE. Throws an error if the user is already active or not found.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User activated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Conflict: User is already active")
    })
    @PatchMapping("/activate")
    @ResponseStatus(HttpStatus.OK)
    public void activateUser(
                              @Parameter(description = "Username of the user to activate", required = true)
                              @Valid @RequestBody UserStatusChangeRequest request
    ) {
        log.info("Received request to activate user: {}", request.getUsername());
        userService.activateUser(request);
    }

    @Operation(summary = "Disable an existing user",
            description = "Sets the status of a specified user to DISABLED. Throws an error if the user is already disabled or not found.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User disabled successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "Conflict: User is already disabled")
    })
    @PatchMapping("/disable")
    @ResponseStatus(HttpStatus.OK)
    public void disableUser(
                             @Parameter(description = "Username of the user to disable", required = true)
                             @Valid @RequestBody UserStatusChangeRequest request // Takes UserStatusChangeRequest
    ) {
        log.info("Received request to disable user: {}", request.getUsername());
        userService.disableUser(request);
    }

    @Operation(summary = "Change a user's password",
            description = "Allows a user to change their password. Requires the old password for verification.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input (e.g., new password matches old)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized: Incorrect old password"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/change-password")
    @ResponseStatus(HttpStatus.OK)
    public PasswordChangeResponse changePassword(
                                                  @Parameter(description = "Details for password change (username, old and new passwords)", required = true)
                                                  @Valid @RequestBody UserChangePasswordRequest request
    ) {
        log.info("Received request to change password for user: {}", request.getUsername());
        return userService.changePassword(request);
    }

    @Operation(summary = "Get all users",
            description = "Retrieves a paginated list of all registered users in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users")
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<UserResponse> getAllUsers(
                                      @Parameter(description = "Page number (0-indexed)", example = "0")
                                      @RequestParam(defaultValue = "0", required = false) Integer page,
                                      @Parameter(description = "Number of items per page", example = "10")
                                      @RequestParam(defaultValue = "10", required = false) Integer size
    ) {
        log.info("Received request to get all users (page: {}, size: {})", page, size);
        return userService.getAllUsers(page, size);
    }
}