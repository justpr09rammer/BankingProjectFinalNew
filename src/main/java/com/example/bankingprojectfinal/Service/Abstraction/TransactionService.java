package com.example.bankingprojectfinal.Service.Abstraction;

import com.example.bankingprojectfinal.DTOS.Transaction.TransactionDto;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Page;
import java.math.BigDecimal;

public interface TransactionService {
    TransactionDto transfer(String debit, String credit, BigDecimal amount);
    Page<TransactionDto> getTransactionsByCustomerId(Integer customerId, Integer page, Integer size);
    Page<TransactionDto> getAllTransactions(Integer page, Integer size);
}
