package com.example.bankingprojectfinal.Repository;

import com.example.bankingprojectfinal.Model.Entity.CustomerEntity;
import com.example.bankingprojectfinal.Model.Enums.CustomerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Integer> {
    Page<CustomerEntity> findByStatus(CustomerStatus status, Pageable pageable);
    boolean existsByFinCode(String finCode);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
}