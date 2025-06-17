package com.example.bankingprojectfinal.Service.Concrete;

import com.example.bankingprojectfinal.Exception.AccountNotActiveException;
// Removed CardNotFoundException as cards are no longer directly debited/credited for balances
import com.example.bankingprojectfinal.Exception.NotEnoughFundsException;
import com.example.bankingprojectfinal.Model.Entity.AccountEntity;
// Removed CardEntity as card-specific balance logic is removed
import com.example.bankingprojectfinal.Model.Entity.CustomerEntity;
import com.example.bankingprojectfinal.Model.Entity.TransactionEntity;
import com.example.bankingprojectfinal.Model.Enums.AccountStatus;
// Removed CardStatus as it's no longer used for balance checks
import com.example.bankingprojectfinal.Model.Enums.CustomerStatus;
import com.example.bankingprojectfinal.Model.Enums.TransactionStatus;
import com.example.bankingprojectfinal.Repository.AccountRepository;
// Removed CardRepository as card-specific balance logic is removed
import com.example.bankingprojectfinal.Repository.CustomerRepository;
import com.example.bankingprojectfinal.Repository.TransactionRepository;
import com.example.bankingprojectfinal.Utils.LimitProperties;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TransactionSchedule {
    TransactionRepository transactionRepository;
    AccountRepository accountRepository;
    CustomerRepository customerRepository;
    LimitProperties limitProperties;

    @Scheduled(cron = "0 0 0 * * *") // Runs daily at midnight
    @Transactional // Ensures atomicity for the batch processing
    public void generateTransactions() {
        log.info("Scheduled task: Starting to process pending transactions at {}. (Account-to-Account Only)", LocalDate.now());

        List<TransactionEntity> pendingTransactions = transactionRepository.findByStatus(TransactionStatus.PENDING);
        if (pendingTransactions.isEmpty()) {
            log.info("No pending transactions found to process.");
            return;
        }

        log.info("Found {} pending transactions to process.", pendingTransactions.size());

        for (TransactionEntity transaction : pendingTransactions) {
            try {
                checkCustomerStatus(transaction.getCustomer());

                updateAccountBalance(transaction.getDebitAccountNumber(), transaction.getAmount(), false); // false for debit

                updateAccountBalance(transaction.getCreditAccountNumber(), transaction.getAmount(), true); // true for credit

                transaction.setStatus(TransactionStatus.COMPLETED);
                transactionRepository.save(transaction);
                log.info("Transaction ID {} successfully processed and marked as COMPLETED.", transaction.getTransactionId());

            } catch (Exception e) {
                log.error("Failed to process transaction ID {}: {}", transaction.getTransactionId(), e.getMessage());
                transaction.setStatus(TransactionStatus.FAILED); // Mark as FAILED
                transactionRepository.save(transaction);
            }
        }
        log.info("Scheduled task: Finished processing pending transactions.");
    }

    private void checkCustomerStatus(CustomerEntity customerEntity) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(1);

        BigDecimal monthlyTotal = transactionRepository.getMonthlyTotalByCustomer(customerEntity.getId(), startDate, endDate);

        if (monthlyTotal != null && monthlyTotal.compareTo(limitProperties.getDailyTransactionLimit()) > 0) {
            customerEntity.setStatus(CustomerStatus.SUSPECTED);
            customerRepository.save(customerEntity);
            log.warn("Customer ID {} detected as SUSPECTED due to monthly transaction total {} exceeding limit {}.",
                    customerEntity.getId(), monthlyTotal, limitProperties.getDailyTransactionLimit());
        }
    }
    private void updateAccountBalance(String accountNumber, BigDecimal amount, boolean isCredit) {
        if (accountNumber.length() != 20) {
            log.error("Invalid account number format for {}: {}. Expected 20 digits for account-to-account transfer.",
                    (isCredit ? "credit" : "debit"), accountNumber);
            throw new IllegalArgumentException("Invalid account number format. Expected 20 digits.");
        }

        AccountEntity account = accountRepository.findById(accountNumber)
                .orElseThrow(() -> {
                    log.error("Account {} not found.", accountNumber);
                    return new AccountNotActiveException("Account not found for processing.");
                });

        if (!account.getStatus().equals(AccountStatus.ACTIVE)) {
            log.error("Account {} is not active.", accountNumber);
            throw new AccountNotActiveException("Account is not active.");
        }

        if (!isCredit) {
            if (account.getBalance().compareTo(amount) < 0) {
                log.error("Insufficient funds in debit account {}. Balance: {}, Required: {}", accountNumber, account.getBalance(), amount);
                throw new NotEnoughFundsException("Not enough funds in debit account.");
            }
            account.setBalance(account.getBalance().subtract(amount));
            log.debug("Debit account {} balance updated. New balance: {}", accountNumber, account.getBalance());
        } else {
            account.setBalance(account.getBalance().add(amount));
            log.debug("Credit account {} balance updated. New balance: {}", accountNumber, account.getBalance());
        }

        accountRepository.save(account);
    }
}