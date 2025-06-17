package com.example.bankingprojectfinal.Controller;

import com.example.bankingprojectfinal.DTOS.Customer.CustomerCreateRequest;
import com.example.bankingprojectfinal.DTOS.Customer.CustomerResponse;
import com.example.bankingprojectfinal.Service.Abstraction.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; // Don't forget to import @Valid for request body validation
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customer Management", description = "APIs for managing bank customers (create, view all)")
public class CustomerController {

    private CustomerService customerService;

    @Operation(summary = "Create a new customer",
               description = "Registers a new customer in the banking system. Requires unique FIN code, email, and phone number.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Customer created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input (e.g., validation errors)"),
            @ApiResponse(responseCode = "409", description = "Conflict: Customer with provided FIN code, email, or phone number already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse createCustomer(
            @Parameter(description = "Customer details for creation", required = true)
            @Valid @RequestBody CustomerCreateRequest customerCreateRequest
    ) {
        return customerService.createCustomer(customerCreateRequest);
    }

    @Operation(summary = "Get all customers",
               description = "Retrieves a paginated list of all registered customers.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of customers"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public Page<CustomerResponse> getAllCustomers(
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0", required = false) Integer page,
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10", required = false) Integer size
    ) {
        return customerService.getAllCustomers(page, size);
    }
}