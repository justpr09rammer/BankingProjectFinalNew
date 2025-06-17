package com.example.bankingprojectfinal.Repository;

import com.example.bankingprojectfinal.Model.Entity.AccountEntity;
import com.example.bankingprojectfinal.Model.Enums.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, String> {
    Integer countByCustomer_IdAndStatusIn(Integer customerId, List<AccountStatus> accountStatusList);
    List<AccountEntity> findByCustomer_IdAndStatus(Integer customerId, AccountStatus accountStatus);
    Page<AccountEntity> findByCustomer_IdAndStatusIn(Integer customerId, List<AccountStatus> accountStatusList, Pageable pageable);
    Page<AccountEntity> findByStatus(AccountStatus status, Pageable pageable);
    Boolean existsByAccountNumber(String accountNumber);
    AccountEntity findByAccountNumber(String accountNumber);

}