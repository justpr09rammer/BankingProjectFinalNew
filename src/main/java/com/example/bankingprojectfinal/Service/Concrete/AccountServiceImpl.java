package com.example.bankingprojectfinal.Service.Concrete;

import com.example.bankingprojectfinal.DTOS.Account.AccountCreateResponse;
import com.example.bankingprojectfinal.DTOS.Account.AccountResponse;
import com.example.bankingprojectfinal.Exception.*;
import com.example.bankingprojectfinal.Model.Entity.AccountEntity;
import com.example.bankingprojectfinal.Model.Entity.CustomerEntity;
import com.example.bankingprojectfinal.Model.Enums.AccountStatus;
import com.example.bankingprojectfinal.Repository.AccountRepository;
import com.example.bankingprojectfinal.Repository.CustomerRepository;
import com.example.bankingprojectfinal.Service.Abstraction.AccountService;
import com.example.bankingprojectfinal.Utils.AccountNumberGenerator;
import com.example.bankingprojectfinal.Utils.LimitProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final LimitProperties limitProperties;
    private final AccountNumberGenerator accountNumberGenerator;
    private final List<AccountStatus> validAccountStatusList = Arrays.asList(AccountStatus.NEW, AccountStatus.ACTIVE);


    @Override
    @Transactional
    public AccountCreateResponse createAccount(Integer customerId) {
        log.info("Attempting to create account for customer ID: {}", customerId);

        CustomerEntity foundCustomer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with ID " + customerId + " not found."));
        log.debug("Customer found: {}", foundCustomer.getId());


        Integer currentAccountCountOfCustomer = accountRepository.countByCustomer_IdAndStatusIn(customerId, validAccountStatusList);
        if (currentAccountCountOfCustomer >= limitProperties.getMaxAccountCountPerCustomer()) {
            log.warn("Customer {} has reached max account limit ({} accounts). Current active/new accounts: {}", customerId, limitProperties.getMaxAccountCountPerCustomer(), currentAccountCountOfCustomer);
            throw new MaximumAccountCountException(limitProperties.getMaxAccountCountPerCustomer());
        }
        String currentAccountNumber;
        do {
            currentAccountNumber = accountNumberGenerator.generate();
        }
        while (accountRepository.existsByAccountNumber(currentAccountNumber));
        AccountEntity createdAccount = AccountEntity.builder()
                .accountNumber(currentAccountNumber)
                .customer(foundCustomer)
                .balance(BigDecimal.ZERO)
                .openingDate(LocalDate.now())
                .expireDate(LocalDate.now().plusYears(10))
                .status(AccountStatus.NEW)
                .transactions(null)
                .cards(null)
                .build();

        AccountEntity savedAccount = accountRepository.save(createdAccount);
        return AccountCreateResponse.builder()
                .accountNumber(savedAccount.getAccountNumber())
                .customerId(savedAccount.getCustomer().getId())
                .balance(savedAccount.getBalance())
                .openingDate(savedAccount.getOpeningDate())
                .expireDate(savedAccount.getExpireDate())
                .status(savedAccount.getStatus())
                .success(true)
                .message("New Account created successfully.")
                .build();
    }

    @Override
    public void activateAccount(String accountNumber) {
        AccountEntity accountEntity = accountRepository.findById(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account with number " + accountNumber + " not found."));

        if (!accountEntity.getStatus().equals(AccountStatus.NEW)) {
            log.warn("Account {} cannot be activated. Current status: {}. Only NEW accounts can be activated.",
                    accountNumber, accountEntity.getStatus());
            throw new InvalidAccountActivationException("Accoutn has already been activated");
        }

        accountEntity.setStatus(AccountStatus.ACTIVE);
        accountRepository.save(accountEntity);

        log.info("Account {} successfully activated.", accountNumber);


    }

    @Override
    @Transactional
    public void depositAccount(String accountNumber, BigDecimal amount) {

        //1 is the given accountNumber valid
        AccountEntity accountEntity = accountRepository.findById(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account with number " + accountNumber + " does not exist."));

        log.debug("Account found for deposit: {} with current balance: {}", accountNumber, accountEntity.getBalance());
        //2 we are checking the given amount is correct or not
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Invalid deposit amount: {}. Amount must be positive.", amount);
            throw new InvalidDepositAmount("Deposit amount must be positive.");
        }
        // 3 we are checking the status of account
        if (!accountEntity.getStatus().equals(AccountStatus.EXPIRED)) {
            log.warn("Deposit failed for account {}. Account is not ACTIVE. Current status: {}",
                    accountNumber, accountEntity.getStatus());
            throw new InactiveAccountDepositException("Account should be active");
        }
        BigDecimal previousBalance = accountEntity.getBalance();
        BigDecimal newBalance = accountEntity.getBalance().add(amount);
        accountEntity.setBalance(newBalance);
        accountRepository.save(accountEntity);

        log.info("Deposited {} to account {}. Previous balance was {} New balance: {}", amount, previousBalance, accountNumber, newBalance);


    }

    public Page<AccountResponse> getAccountsByCustomerId(Integer customerId, Integer page, Integer size) {
        log.info("Fetching accounts for customer ID: {} (page: {}, size: {})", customerId, page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<AccountEntity> accountEntityPage = accountRepository.findByCustomer_IdAndStatusIn(customerId, validAccountStatusList, pageable);

        List<AccountResponse> accountResponseList = accountEntityPage.getContent().stream()
                .map(entity -> AccountResponse.builder()
                        .accountNumber(entity.getAccountNumber())
                        .customerId(entity.getCustomer().getId())
                        .balance(entity.getBalance())
                        .openingDate(entity.getOpeningDate())
                        .expireDate(entity.getExpireDate())
                        .status(entity.getStatus())
                        .build())
                .collect(Collectors.toList());
        log.debug("Found {} accounts for customer ID: {}", accountResponseList.size(), customerId);

        return new PageImpl<>(accountResponseList, accountEntityPage.getPageable(), accountEntityPage.getTotalElements());
    }


    @Override
    public Page<AccountResponse> getAllActiveAccounts(Integer page, Integer size) {
        log.info("Fetching all active accounts (page: {}, size: {})", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<AccountEntity> accountEntityPage = accountRepository.findByStatus(AccountStatus.ACTIVE, pageable);
        List<AccountResponse> accountResponseList = accountEntityPage.getContent().stream()
                .map(entity -> AccountResponse.builder()
                        .accountNumber(entity.getAccountNumber())
                        .customerId(entity.getCustomer().getId())
                        .balance(entity.getBalance())
                        .openingDate(entity.getOpeningDate())
                        .expireDate(entity.getExpireDate())
                        .status(entity.getStatus())
                        .build())
                .collect(Collectors.toList());
        log.debug("Found {} active accounts.", accountResponseList.size());

        return new PageImpl<>(accountResponseList, pageable, accountEntityPage.getTotalElements());
    }

    @Override
    public Page<AccountResponse> getAllAccounts(Integer page, Integer size) {
        log.info("Fetching all accounts (page: {}, size: {})", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<AccountEntity> accountEntityPage = accountRepository.findAll(pageable);

        List<AccountResponse> accountResponseList = accountEntityPage.getContent().stream()
                .map(entity -> AccountResponse.builder()
                        .accountNumber(entity.getAccountNumber())
                        .customerId(entity.getCustomer().getId())
                        .balance(entity.getBalance())
                        .openingDate(entity.getOpeningDate())
                        .expireDate(entity.getExpireDate())
                        .status(entity.getStatus())
                        .build())
                .collect(Collectors.toList());
        log.debug("Found {} total accounts.", accountResponseList.size());

        return new PageImpl<>(accountResponseList, pageable, accountEntityPage.getTotalElements());
    }
    @Override
    public Page<AccountResponse> getAllExpiredAccounts(Integer page, Integer size) {
        log.info("Fetching all expired accounts (page: {}, size: {})", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<AccountEntity> accountEntityPage = accountRepository.findByStatus(AccountStatus.EXPIRED, pageable);

        List<AccountResponse> accountResponseList = accountEntityPage.getContent().stream()
                .map(entity -> AccountResponse.builder()
                        .accountNumber(entity.getAccountNumber())
                        .customerId(entity.getCustomer().getId())
                        .balance(entity.getBalance())
                        .openingDate(entity.getOpeningDate())
                        .expireDate(entity.getExpireDate())
                        .status(entity.getStatus())
                        .build())
                .collect(Collectors.toList());
        log.debug("Found {} expired accounts.", accountResponseList.size());

        return new PageImpl<>(accountResponseList, pageable, accountEntityPage.getTotalElements());
    }
}