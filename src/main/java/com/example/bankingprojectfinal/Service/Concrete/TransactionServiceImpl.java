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
public class TransactionServiceImpl implements TransactionService {
    TransactionRepository transactionRepository;
    TransactionMapper transactionMapper;
    AccountRepository accountRepository;
    CardRepository cardRepository;
    LimitProperties limitProperties;

    @Override
    public TransactionDto transfer(String debit, String credit, BigDecimal amount) {
        checkCreditNumber(credit);

        if (debit.length() == 16) {
            return transferFromAccount(debit, credit, amount);
        } else if (debit.length() == 20) {
            return transferFromAccount(debit, credit, amount);
        } else
            throw new IllegalArgumentException("Wrong input for debit");
    }

    private TransactionDto transferFromAccount(String debit, String credit, BigDecimal amount) {
        AccountEntity accountEntity = accountRepository.findById(debit).orElseThrow();

        if (!accountEntity.getStatus().equals(AccountStatus.ACTIVE))
            throw new AccountNotActiveException("Account should be active");

        CustomerEntity customerEntity = accountEntity.getCustomer();
        checkCustomerTransactions(customerEntity);

        if (accountEntity.getBalance().compareTo(amount) < 0)
            throw new NotEnoughFundsException("you should have enough amount of oney");

        if (accountEntity.getBalance().compareTo(limitProperties.getMinAcceptableAccountBalance()) < 0)
            throw new LimitExceedsException("your transfer exceeds the limit");

        return createTransaction(customerEntity, debit, credit, amount);
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
        if (credit.length() != 20 && credit.length() != 16)
            throw new IllegalArgumentException("Wrong input for credit");
    }

    private TransactionDto createTransaction(CustomerEntity customerEntity,
                                             String debit, String credit,
                                             BigDecimal amount) {
        TransactionEntity transactionEntity = transactionMapper.buildTransactionEntity(customerEntity, debit, credit, amount);

        transactionRepository.save(transactionEntity);
        return transactionMapper.getTransactionDto(transactionEntity);
    }

    @Override
    public Page<TransactionDto> getTransactionsByCustomerId(Integer customerId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TransactionEntity> transactionEntityPage = new Page<TransactionEntity>() {
            @Override
            public int getTotalPages() {
                return 0;
            }

            @Override
            public long getTotalElements() {
                return 0;
            }

            @Override
            public <U> Page<U> map(Function<? super TransactionEntity, ? extends U> converter) {
                return null;
            }

            @Override
            public int getNumber() {
                return 0;
            }

            @Override
            public int getSize() {
                return 0;
            }

            @Override
            public int getNumberOfElements() {
                return 0;
            }

            @Override
            public List<TransactionEntity> getContent() {
                return List.of();
            }

            @Override
            public boolean hasContent() {
                return false;
            }

            @Override
            public Sort getSort() {
                return null;
            }

            @Override
            public boolean isFirst() {
                return false;
            }

            @Override
            public boolean isLast() {
                return false;
            }

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

            @Override
            public Pageable nextPageable() {
                return null;
            }

            @Override
            public Pageable previousPageable() {
                return null;
            }

            @Override
            public Iterator<TransactionEntity> iterator() {
                return null;
            }
        };
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
