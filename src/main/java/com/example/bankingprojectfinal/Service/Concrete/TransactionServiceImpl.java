package com.example.bankingprojectfinal.Service.Concrete;

import com.example.bankingprojectfinal.DTOS.Transaction.TransactionDto;
import com.example.bankingprojectfinal.DTOS.Transaction.TransactionMapper;
import com.example.bankingprojectfinal.Exception.AccountNotActiveException;
import com.example.bankingprojectfinal.Exception.CardNotFoundException;
import com.example.bankingprojectfinal.Exception.LimitExceedsException;
import com.example.bankingprojectfinal.Exception.NotEnoughFundsException;
import com.example.bankingprojectfinal.Model.Entity.AccountEntity;
import com.example.bankingprojectfinal.Model.Entity.CardEntity;
import com.example.bankingprojectfinal.Model.Entity.CustomerEntity;
import com.example.bankingprojectfinal.Model.Entity.TransactionEntity;
import com.example.bankingprojectfinal.Model.Enums.AccountStatus;
import com.example.bankingprojectfinal.Repository.AccountRepository;
import com.example.bankingprojectfinal.Repository.CardRepository;
import com.example.bankingprojectfinal.Repository.TransactionRepository;
import com.example.bankingprojectfinal.Service.Abstraction.TransactionService;
import com.example.bankingprojectfinal.Utils.LimitProperties;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@org.springframework.transaction.annotation.Transactional
public class TransactionServiceImpl implements TransactionService {
    TransactionRepository transactionRepository;
    TransactionMapper transactionMapper;
    AccountRepository accountRepository;
    CardRepository cardRepository;
    LimitProperties limitProperties;

    @Override
    public TransactionDto transfer(String debit, String credit, BigDecimal amount) {
        if (debit == null || credit == null) {
            throw new IllegalArgumentException("Account numbers must not be null");
        }
        if (!debit.matches("\\d{20}") || !credit.matches("\\d{20}")) {
            throw new IllegalArgumentException("Debit and credit account numbers must be 20 digits");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        return transferFromAccount(debit, credit, amount);
    }

    private TransactionDto transferFromAccount(String debit, String credit, BigDecimal amount) {
        // Lock accounts in a consistent order to avoid deadlocks
        String first = debit.compareTo(credit) <= 0 ? debit : credit;
        String second = debit.compareTo(credit) <= 0 ? credit : debit;

        AccountEntity firstLocked = accountRepository.lockByAccountNumber(first)
                .orElseThrow(() -> new AccountNotActiveException("Account not found: " + first));
        AccountEntity secondLocked = accountRepository.lockByAccountNumber(second)
                .orElseThrow(() -> new AccountNotActiveException("Account not found: " + second));

        AccountEntity debitAccount = debit.equals(first) ? firstLocked : secondLocked;
        AccountEntity creditAccount = credit.equals(second) ? secondLocked : firstLocked;

        if (!debitAccount.getStatus().equals(AccountStatus.ACTIVE)) {
            throw new AccountNotActiveException("Debit account should be active");
        }
        if (!creditAccount.getStatus().equals(AccountStatus.ACTIVE)) {
            throw new AccountNotActiveException("Credit account should be active");
        }

        CustomerEntity customerEntity = debitAccount.getCustomer();
        checkCustomerTransactions(customerEntity);

        if (debitAccount.getBalance().compareTo(amount) < 0) {
            throw new NotEnoughFundsException("Not enough funds");
        }
        if (debitAccount.getBalance().subtract(amount)
                .compareTo(limitProperties.getMinAcceptableAccountBalance()) < 0) {
            throw new LimitExceedsException("Transfer would drop balance below minimum acceptable");
        }

        debitAccount.setBalance(debitAccount.getBalance().subtract(amount));
        creditAccount.setBalance(creditAccount.getBalance().add(amount));
        accountRepository.save(debitAccount);
        accountRepository.save(creditAccount);

        TransactionEntity transactionEntity = TransactionEntity.builder()
                .customer(customerEntity)
                .account(debitAccount)
                .debitAccountNumber(debit)
                .creditAccountNumber(credit)
                .transactionDate(LocalDate.now())
                .amount(amount)
                .status(com.example.bankingprojectfinal.Model.Enums.TransactionStatus.COMPLETED)
                .transactionType(com.example.bankingprojectfinal.Model.Enums.TransactionType.TRANSFER)
                .build();

        transactionRepository.save(transactionEntity);
        return transactionMapper.getTransactionDto(transactionEntity);
    }
//
//    private TransactionDto transferFromAccountWhenCardFailed(CustomerEntity customerEntity, String credit, BigDecimal amount) {
//        Integer customerId = customerEntity.getId();
//        List<AccountEntity> accountEntityList = accountRepository.find
//
//        if (accountEntityList.isEmpty()) {
//            throw new NotEnoughFundsException("No active accounts available");
//        }
//
//        AccountEntity accountEntityWithMaxBalance = null;
//        for (AccountEntity account : accountEntityList) {
//            if (accountEntityWithMaxBalance == null ||
//                    account.getBalance().compareTo(accountEntityWithMaxBalance.getBalance()) > 0) {
//                accountEntityWithMaxBalance = account;
//            }
//        }
//
//        if (accountEntityWithMaxBalance == null ||
//                accountEntityWithMaxBalance.getBalance().compareTo(amount) < 0) {
//            throw new NotEnoughFundsException("Not enough funds in any account");
//        }
//
//        return createTransaction(customerEntity, accountEntityWithMaxBalance.getAccountNumber(), credit, amount);
//    }

    private void checkCustomerTransactions(CustomerEntity customerEntity) {

//        if (!transactionRepository.findByCustomerEntityAndTransactionDate(customerEntity, LocalDate.now()).isEmpty())
//            throw new LimitExceedsException("exceeds the limit");
    }

    private void checkCreditNumber(String credit) {
        if (credit == null || !credit.matches("\\d{20}"))
            throw new IllegalArgumentException("Wrong input for credit; must be 20 digits");
    }


    @Override
    public Page<TransactionDto> getTransactionsByCustomerId(Integer customerId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TransactionEntity> transactionEntityPage = transactionRepository.findByAccount_Customer_Id(customerId, pageable);
        List<TransactionDto> transactionDtoList = transactionMapper.getTransactionDtoList(transactionEntityPage.getContent());
        return new PageImpl<>(transactionDtoList, pageable, transactionEntityPage.getTotalElements());
    }

    @Override
    public Page<TransactionDto> getAllTransactions(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TransactionEntity> transactionEntities = transactionRepository.findAll(pageable);
        List<TransactionDto> transactionDtoList = transactionMapper.getTransactionDtoList(transactionEntities.getContent());
        return new PageImpl<>(transactionDtoList, pageable, transactionEntities.getTotalElements());
    }
}
