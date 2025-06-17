package com.example.bankingprojectfinal.Model.Enums;

public enum TransactionType {
    DEBIT,          // Money leaving the account (e.g., withdrawal, payment)
    CREDIT,         // Money entering the account (e.g., deposit, interest)
    TRANSFER,       // Money moved between accounts (can be both a debit and a credit entry, or a specific type)
    DEPOSIT,        // Specific type of credit
}