package com.example.bankingprojectfinal.Repository;

import com.example.bankingprojectfinal.Model.Entity.CustomerEntity;
import com.example.bankingprojectfinal.Model.Entity.TransactionEntity;
import com.example.bankingprojectfinal.Model.Enums.TransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.example.bankingprojectfinal.Model.Entity.AccountEntity; // Import AccountEntity
import com.example.bankingprojectfinal.Model.Entity.CustomerEntity; // Import CustomerEntity
import com.example.bankingprojectfinal.Model.Entity.TransactionEntity;
import com.example.bankingprojectfinal.Model.Enums.TransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, String> {

    // --- REPLACED: findByCustomer_Id is no longer valid ---
    // New method to find transactions by the customer associated with the 'account' field
    // This will query transactions where transaction.account.customer.id matches customerId
    Page<TransactionEntity> findByAccount_Customer_Id(Integer customerId, Pageable pageable);

    List<TransactionEntity> findByStatus(TransactionStatus status);

    // --- REPLACED: findByCustomerAndTransactionDate is no longer valid ---
    // New method to find transactions by customer (via account) and transaction date.
    // This will find transactions where transaction.account.customer.id matches customerId
    // AND the transactionDate matches the provided date.
    List<TransactionEntity> findByAccount_Customer_IdAndTransactionDate(Integer customerId, LocalDate date);


    // --- REPLACED: getMonthlyTotalByCustomer query updated ---
    // The query now explicitly navigates through 'account' to 'customer'
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM TransactionEntity t WHERE t.account.customer.id = :customerId AND t.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal getMonthlyTotalByCustomer(
            @Param("customerId") Integer customerId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // You might also want methods to find transactions by debit/credit account numbers directly
    Optional<TransactionEntity> findByDebitAccountNumber(String debitAccountNumber);
    Optional<TransactionEntity> findByCreditAccountNumber(String creditAccountNumber);

    // To get all transactions related to any account of a customer (either debit or credit)
    // This requires a more complex query if 'account' only links to one side.
    // Example using OR, assuming you want to fetch all transactions a customer is involved in:

}