package com.example.bankingprojectfinal.Service.Abstraction;

import com.example.bankingprojectfinal.DTOS.Customer.CustomerCreateRequest;
import com.example.bankingprojectfinal.DTOS.Customer.CustomerDto;
import com.example.bankingprojectfinal.DTOS.Customer.CustomerResponse;
import org.springframework.data.domain.Page;



public interface CustomerService {
    CustomerResponse createCustomer(CustomerCreateRequest customerCreateRequest);
    Page<CustomerResponse> getAllCustomers(Integer page, Integer size);
}