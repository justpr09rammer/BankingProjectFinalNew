package com.example.bankingprojectfinal.Exception;

public class InvalidCardStatusException extends RuntimeException {
    public InvalidCardStatusException(String message) {
        super(message);
    }
}
