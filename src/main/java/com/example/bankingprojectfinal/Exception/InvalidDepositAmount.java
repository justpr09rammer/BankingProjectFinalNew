package com.example.bankingprojectfinal.Exception;

public class InvalidDepositAmount extends RuntimeException {
    public InvalidDepositAmount(String message) {
        super(message);
    }
}
