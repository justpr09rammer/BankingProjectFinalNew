package com.example.bankingprojectfinal.Exception;

public class LimitExceedsException extends RuntimeException {
    public LimitExceedsException(String message) {
        super(message);
    }
}
