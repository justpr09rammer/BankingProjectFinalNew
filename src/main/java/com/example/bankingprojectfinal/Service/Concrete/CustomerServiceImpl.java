package com.example.bankingprojectfinal.Service.Concrete;

import com.example.bankingprojectfinal.DTOS.Customer.CustomerCreateRequest;
import com.example.bankingprojectfinal.DTOS.Customer.CustomerResponse;
import com.example.bankingprojectfinal.Exception.DuplicateResourceException; // Assuming you have this custom exception
import com.example.bankingprojectfinal.Model.Entity.CustomerEntity;
import com.example.bankingprojectfinal.Model.Enums.CustomerStatus;
import com.example.bankingprojectfinal.Repository.CustomerRepository;
import com.example.bankingprojectfinal.Service.Abstraction.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    private CustomerResponse convertToCustomerResponse(CustomerEntity customerEntity) {
        if (customerEntity == null) {
            return null;
        }
        return CustomerResponse.builder()
                .customerId(customerEntity.getId())
                .firstName(customerEntity.getFirstName())
                .lastName(customerEntity.getLastName())
                .birthDate(customerEntity.getBirthDate())
                .finCode(customerEntity.getFinCode())
                .phoneNumber(customerEntity.getPhoneNumber())
                .email(customerEntity.getEmail())
                .registrationDate(customerEntity.getRegistrationDate())
                .status(customerEntity.getStatus())
                .build();
    }

    @Override
    @Transactional
    public CustomerResponse createCustomer(CustomerCreateRequest customerCreateRequest) {
        log.info("Attempting to create a new customer with FIN: {}", customerCreateRequest.getFinCode());

        // Validate for uniqueness before creating
        if (customerRepository.existsByFinCode(customerCreateRequest.getFinCode())) {
            log.warn("Customer creation failed: Duplicate FIN code found: {}", customerCreateRequest.getFinCode());
            throw new DuplicateResourceException("Customer with FIN code " + customerCreateRequest.getFinCode() + " already exists.");
        }
        if (customerRepository.existsByEmail(customerCreateRequest.getEmail())) {
            log.warn("Customer creation failed: Duplicate email found: {}", customerCreateRequest.getEmail());
            throw new DuplicateResourceException("Customer with email " + customerCreateRequest.getEmail() + " already exists.");
        }
        if (customerRepository.existsByPhoneNumber(customerCreateRequest.getPhoneNumber())) {
            log.warn("Customer creation failed: Duplicate phone number found: {}", customerCreateRequest.getPhoneNumber());
            throw new DuplicateResourceException("Customer with phone number " + customerCreateRequest.getPhoneNumber() + " already exists.");
        }

        try {
            CustomerEntity customerEntity = CustomerEntity.builder()
                    .firstName(customerCreateRequest.getFirstName())
                    .lastName(customerCreateRequest.getLastName())
                    .birthDate(customerCreateRequest.getBirthDate())
                    .finCode(customerCreateRequest.getFinCode())
                    .phoneNumber(customerCreateRequest.getPhoneNumber())
                    .email(customerCreateRequest.getEmail())
                    .registrationDate(LocalDate.now())
                    .status(CustomerStatus.REGULAR)
                    .build();
            CustomerEntity savedCustomer = customerRepository.save(customerEntity);
            log.info("Customer created successfully with ID: {} and FIN: {}", savedCustomer.getId(), savedCustomer.getFinCode());

            return convertToCustomerResponse(savedCustomer);

        } catch (DuplicateResourceException e) {
            throw e;
        } catch (Exception e) {
            log.error("An unexpected error occurred during customer creation: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create customer due to an internal error.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerResponse> getAllCustomers(Integer page, Integer size) {
        log.info("Fetching all customers (page: {}, size: {})", page, size);
        PageRequest pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        Page<CustomerEntity> customerEntities = customerRepository.findAll(pageable);
        return customerEntities.map(this::convertToCustomerResponse);
    }
}