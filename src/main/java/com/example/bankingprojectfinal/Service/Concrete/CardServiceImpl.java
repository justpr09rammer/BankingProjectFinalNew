package com.example.bankingprojectfinal.Service.Concrete;

import com.example.bankingprojectfinal.DTOS.Card.*;
import com.example.bankingprojectfinal.Exception.*;
import com.example.bankingprojectfinal.Model.Entity.AccountEntity;
import com.example.bankingprojectfinal.Model.Entity.CardEntity;
import com.example.bankingprojectfinal.Model.Entity.TransactionEntity;
import com.example.bankingprojectfinal.Model.Enums.AccountStatus;
import com.example.bankingprojectfinal.Model.Enums.CardStatus;
import com.example.bankingprojectfinal.Model.Enums.TransactionStatus;
import com.example.bankingprojectfinal.Model.Enums.TransactionType; // Ensure this is imported
import com.example.bankingprojectfinal.Repository.AccountRepository;
import com.example.bankingprojectfinal.Repository.CardRepository;
import com.example.bankingprojectfinal.Repository.TransactionRepository;
import com.example.bankingprojectfinal.Service.Abstraction.CardService;
import com.example.bankingprojectfinal.Utils.CardNumberGenerator;
import com.example.bankingprojectfinal.Utils.LimitProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest; // For creating Pageable instances
import org.springframework.data.domain.Sort;       // For sorting options in Pageable
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardServiceImpl implements CardService {

    private final TransactionRepository transactionRepository;
    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final List<CardStatus> validCardStatusList = Arrays.asList(CardStatus.NEW, CardStatus.ACTIVE);
    private final LimitProperties limitProperties;
    private final CardNumberGenerator cardNumberGenerator;

    @Override
    @Transactional
    public CardCreateResponse createCard(CreateCardRequest cardRequest) {
        String accountNumber = cardRequest.getAccountNumber();
        log.info("Attempting to create card for account number: {}", accountNumber);

        try {
            if (!accountRepository.existsByAccountNumber(accountNumber)) {
                throw new AccountNotFoundException("The given account number is not valid: " + accountNumber);
            }

            AccountEntity accountEntity = accountRepository.findByAccountNumber(accountNumber);
            log.debug("Account found: {} for card creation.", accountEntity.getAccountNumber());

            if (!accountEntity.getStatus().equals(AccountStatus.ACTIVE) &&
                    !accountEntity.getStatus().equals(AccountStatus.NEW)) {
                log.warn("Card cannot be created for account {}. Current status: {}. Only NEW or ACTIVE accounts are allowed.",
                        accountNumber, accountEntity.getStatus());
                throw new InvalidAccountStatusException(
                        "Card cannot be created for account in " + accountEntity.getStatus() + " status. Only NEW or ACTIVE accounts are allowed."
                );
            }

            Integer currentCardsOnThisAccount = cardRepository.countByAccount_AccountNumberAndStatusIn(accountNumber, validCardStatusList);
            if (currentCardsOnThisAccount >= limitProperties.getMaxCardCountPerAccount()) {
                log.warn("Account {} has reached its maximum card limit ({} cards). Current new/active cards: {}",
                        accountNumber, limitProperties.getMaxCardCountPerAccount(), currentCardsOnThisAccount);
                throw new MaximumCardCoundException("Account " + accountNumber + " has reached its maximum card limit of " + limitProperties.getMaxCardCountPerAccount() + " cards.");
            }

            String currentCardNumber;
            do {
                currentCardNumber = cardNumberGenerator.generate();
            } while (cardRepository.existsByCardNumber(currentCardNumber));

            CardEntity cardEntity = CardEntity.builder()
                    .cardNumber(currentCardNumber)
                    .account(accountEntity)
                    .issueDate(LocalDate.now())
                    .expireDate(LocalDate.now().plusYears(5))
                    .status(CardStatus.NEW)
                    .build();

            CardEntity savedCardEntity = cardRepository.save(cardEntity);
            log.info("Card {} created and linked to account {}.", savedCardEntity.getCardNumber(), savedCardEntity.getAccount().getAccountNumber());

            CardDto createdCardDto = CardDto.builder()
                    .cardNumber(savedCardEntity.getCardNumber())
                    .accountNumber(savedCardEntity.getAccount().getAccountNumber())
                    .issueDate(savedCardEntity.getIssueDate())
                    .expireDate(savedCardEntity.getExpireDate())
                    .status(savedCardEntity.getStatus())
                    .balance(savedCardEntity.getAccount().getBalance())
                    .build();

            return CardCreateResponse.builder()
                    .success(true)
                    .message("Card created successfully for account " + accountNumber + ".")
                    .card(createdCardDto)
                    .build();

        } catch (AccountNotFoundException | InvalidAccountStatusException | MaximumCardCoundException e) {
            log.error("Failed to create card for account {}: {}", accountNumber, e.getMessage());
            return CardCreateResponse.builder()
                    .success(false)
                    .message("Card creation failed: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("An unexpected error occurred during card creation for account {}: {}", accountNumber, e.getMessage(), e);
            return CardCreateResponse.builder()
                    .success(false)
                    .message("An unexpected error occurred during card creation.")
                    .build();
        }
    }


    @Override
    @Transactional
    public ActivateCardResponse activateCard(ActivateCardRequest request) {
        String cardNumber = request.getCardNumber();
        log.info("Attempting to activate card with number: {}", cardNumber);

        try {
            CardEntity cardEntity = cardRepository.findById(cardNumber)
                    .orElseThrow(() -> new CardNotFoundException("Card with number " + cardNumber + " not found."));
            log.debug("Card found: {} for activation. Current status: {}", cardNumber, cardEntity.getStatus());

            if (!cardEntity.getStatus().equals(CardStatus.NEW)) {
                log.warn("Card {} cannot be activated. Current status: {}. Only NEW cards can be activated.",
                        cardNumber, cardEntity.getStatus());
                throw new InvalidCardActivationException("Card has already been activated or is not in NEW status. Current status: " + cardEntity.getStatus());
            }
            LocalDate previousExpireDate = cardEntity.getExpireDate();
            cardEntity.setStatus(CardStatus.ACTIVE);
            cardEntity.setExpireDate(LocalDate.now().plusYears(5)); // Set expire date to 5 years from NOW (activation date)
            CardEntity updatedCardEntity = cardRepository.save(cardEntity);
            log.info("Card {} successfully activated.", cardNumber);

            CardDto activatedCardDto = CardDto.builder()
                    .cardNumber(updatedCardEntity.getCardNumber())
                    .accountNumber(updatedCardEntity.getAccount().getAccountNumber())
                    .issueDate(updatedCardEntity.getIssueDate())
                    .expireDate(updatedCardEntity.getExpireDate())
                    .status(updatedCardEntity.getStatus())
                    .balance(updatedCardEntity.getAccount().getBalance())
                    .build();

            return ActivateCardResponse.builder()
                    .success(true)
                    .previousExpireDate(previousExpireDate)
                    .message("Card " + cardNumber + " activated successfully!")
                    .card(activatedCardDto)
                    .build();

        } catch (CardNotFoundException | InvalidCardActivationException e) {
            log.error("Failed to activate card {}: {}", cardNumber, e.getMessage());
            return ActivateCardResponse.builder()
                    .success(false)
                    .message("Card activation failed: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("An unexpected error occurred during card activation for card {}: {}", cardNumber, e.getMessage(), e);
            return ActivateCardResponse.builder()
                    .success(false)
                    .message("An unexpected error occurred during card activation.")
                    .build();
        }
    }

    @Override
    @Transactional
    public DepositCardResponse depositCard(DepositCardRequest request) {
        String cardNumber = request.getCardNumber();
        BigDecimal amount = request.getAmount();
        log.info("Attempting to deposit {} to card number: {}", amount, cardNumber);

        try {
            CardEntity cardEntity = cardRepository.findById(cardNumber)
                    .orElseThrow(() -> new CardNotFoundException("Card with number " + cardNumber + " not found."));
            log.debug("Card found: {} for deposit.", cardNumber);

            // Best practice: Explicitly check for ACTIVE status, not just non-EXPIRED
            if (!cardEntity.getStatus().equals(CardStatus.ACTIVE)) {
                log.warn("Deposit failed for card {}. Card status is {}. Only ACTIVE cards can be used for deposits.",
                        cardNumber, cardEntity.getStatus());
                throw new InvalidCardStatusException("Card is not active. Current status: " + cardEntity.getStatus() + ". Only ACTIVE cards can be used for deposits.");
            }

            AccountEntity accountEntity = cardEntity.getAccount();
            if (accountEntity == null) {
                log.error("Card {} is not linked to any account.", cardNumber);
                throw new AccountNotFoundException("Account linked to card " + cardNumber + " not found.");
            }

            // Best practice: Explicitly check for ACTIVE account status
            if (!accountEntity.getStatus().equals(AccountStatus.ACTIVE)) {
                log.warn("Deposit failed for account {}. Account status is {}. Only ACTIVE accounts can receive deposits.",
                        accountEntity.getAccountNumber(), accountEntity.getStatus());
                throw new InvalidAccountStatusException("Account is not active. Current status: " + accountEntity.getStatus() + ". Only ACTIVE accounts can receive deposits.");
            }


            BigDecimal currentBalance = accountEntity.getBalance();
            BigDecimal newBalance = currentBalance.add(amount);
            accountEntity.setBalance(newBalance);
            accountRepository.save(accountEntity);
            log.info("Successfully deposited {} to account {}. New balance: {}", amount, accountEntity.getAccountNumber(), newBalance);

            TransactionEntity transaction = TransactionEntity.builder()
                    .amount(amount)
                    .transactionType(TransactionType.DEPOSIT)
                    .transactionDate(LocalDate.now())
                    .creditAccountNumber(accountEntity.getAccountNumber())
                    .debitAccountNumber("CARD_DEPOSIT")
                    .status(TransactionStatus.COMPLETED)
                    .account(accountEntity)
                    .customer(accountEntity.getCustomer())
                    .build();

            TransactionEntity savedTransaction = transactionRepository.save(transaction);
            log.info("Deposit transaction recorded with ID: {}", savedTransaction.getTransactionId());

            return DepositCardResponse.builder()
                    .success(true)
                    .message("Successfully deposited " + amount + " to card " + cardNumber + ".")
                    .cardNumber(cardNumber)
                    .depositedAmount(amount)
                    .newAccountBalance(newBalance)
                    .transactionId(savedTransaction.getTransactionId())
                    .transactionTimestamp(LocalDateTime.now())
                    .build();

        } catch (CardNotFoundException | InvalidCardStatusException | AccountNotFoundException | InvalidAccountStatusException e) {
            log.error("Deposit failed for card {}: {}", cardNumber, e.getMessage());
            return DepositCardResponse.builder()
                    .success(false)
                    .message("Deposit failed: " + e.getMessage())
                    .cardNumber(cardNumber)
                    .depositedAmount(amount)
                    .newAccountBalance(BigDecimal.ZERO)
                    .transactionTimestamp(LocalDateTime.now())
                    .build();
        } catch (Exception e) {
            log.error("An unexpected error occurred during deposit for card {}: {}", cardNumber, e.getMessage(), e);
            return DepositCardResponse.builder()
                    .success(false)
                    .message("An unexpected error occurred during deposit.")
                    .cardNumber(cardNumber)
                    .depositedAmount(amount)
                    .newAccountBalance(BigDecimal.ZERO)
                    .transactionTimestamp(LocalDateTime.now())
                    .build();
        }
    }

    private CardDto convertToCardDto(CardEntity cardEntity) {
        if (cardEntity == null) {
            return null;
        }
        String accountNumber = null;
        BigDecimal balance = BigDecimal.ZERO;

        if (cardEntity.getAccount() != null) {
            accountNumber = cardEntity.getAccount().getAccountNumber();
            balance = cardEntity.getAccount().getBalance();
        }

        return CardDto.builder()
                .cardNumber(cardEntity.getCardNumber())
                .accountNumber(accountNumber)
                .issueDate(cardEntity.getIssueDate())
                .expireDate(cardEntity.getExpireDate())
                .status(cardEntity.getStatus())
                .balance(balance)
                .build();
    }

    @Override
    @Transactional(readOnly = true) // Read-only methods can be marked as such for optimization
    public Page<CardDto> getCardsByAccount(String accountNumber, Integer page, Integer size) {
        log.info("Fetching cards for account number: {} (page: {}, size: {})", accountNumber, page, size);
        PageRequest pageable = PageRequest.of(page, size, Sort.by("issueDate").descending());

        if (!accountRepository.existsByAccountNumber(accountNumber)) {
            log.warn("Account not found when fetching cards: {}", accountNumber);
            return Page.empty(pageable); // Return an empty page if account does not exist
        }

        Page<CardEntity> cardEntities = cardRepository.findByAccount_AccountNumber(accountNumber, pageable);
        return cardEntities.map(this::convertToCardDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CardDto> getCardsByCustomerId(Integer customerId, Integer page, Integer size) {
        log.info("Fetching cards for customer ID: {} (page: {}, size: {})", customerId, page, size);
        PageRequest pageable = PageRequest.of(page, size, Sort.by("issueDate").descending());

        Page<CardEntity> cardEntities = cardRepository.findByAccount_Customer_Id(customerId, pageable);
        return cardEntities.map(this::convertToCardDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CardDto> getAllActiveCards(Integer page, Integer size) {
        log.info("Fetching all active cards (page: {}, size: {})", page, size);
        PageRequest pageable = PageRequest.of(page, size, Sort.by("issueDate").descending());

        Page<CardEntity> cardEntities = cardRepository.findByStatus(CardStatus.ACTIVE, pageable);
        return cardEntities.map(this::convertToCardDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CardDto> getAllCards(Integer page, Integer size) {
        log.info("Fetching all cards (page: {}, size: {})", page, size);
        PageRequest pageable = PageRequest.of(page, size, Sort.by("issueDate").descending());

        Page<CardEntity> cardEntities = cardRepository.findAll(pageable);
        return cardEntities.map(this::convertToCardDto);
    }
}