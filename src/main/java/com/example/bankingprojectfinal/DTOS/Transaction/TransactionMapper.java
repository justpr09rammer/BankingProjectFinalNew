package com.example.bankingprojectfinal.DTOS.Transaction;

import com.example.bankingprojectfinal.Model.Entity.CustomerEntity;
import com.example.bankingprojectfinal.Model.Entity.TransactionEntity;
import com.example.bankingprojectfinal.Model.Enums.TransactionStatus;
import org.mapstruct.Mapper;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    TransactionDto mapToTransactionDto(TransactionEntity transactionEntity);

    default TransactionEntity buildTransactionEntity(CustomerEntity customerEntity, String debit, String credit, BigDecimal amount) {
        return TransactionEntity.builder()
                .transactionId(generateTransactionId())
                .customer(customerEntity)
                .debitAccountNumber(debit)
                .creditAccountNumber(credit)
                .transactionDate(LocalDate.now())
                .amount(amount)
                .status(TransactionStatus.PENDING)
                .build();
    }

    private String generateTransactionId() {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder("TR");
        for (int i = 0; i < 18; i++) {
            stringBuilder.append(random.nextInt(10));
        }
        return stringBuilder.toString();
    }

    default TransactionDto getTransactionDto(TransactionEntity transactionEntity) {
        TransactionDto transactionDto = mapToTransactionDto(transactionEntity);
        transactionDto.setCustomerId(transactionEntity.getCustomer().getId());
        return transactionDto;
    }

    default List<TransactionDto> getTransactionDtoList(List<TransactionEntity> transactionEntities) {
        List<TransactionDto> transactionDtoList = new ArrayList<>();
        for (TransactionEntity transactionEntity : transactionEntities) {
            transactionDtoList.add(getTransactionDto(transactionEntity));
        }
        return transactionDtoList;
    }
}
