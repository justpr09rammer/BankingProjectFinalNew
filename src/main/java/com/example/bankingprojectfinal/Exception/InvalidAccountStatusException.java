package com.example.bankingprojectfinal.Exception;

public class InvalidAccountStatusException extends RuntimeException {
    public InvalidAccountStatusException(String message) {
        super(message);
    }
}
