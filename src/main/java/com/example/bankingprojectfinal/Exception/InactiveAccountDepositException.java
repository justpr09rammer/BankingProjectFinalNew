package com.example.bankingprojectfinal.Exception;

public class InactiveAccountDepositException extends RuntimeException {
    public InactiveAccountDepositException(String message) {
        super(message);
    }
}
