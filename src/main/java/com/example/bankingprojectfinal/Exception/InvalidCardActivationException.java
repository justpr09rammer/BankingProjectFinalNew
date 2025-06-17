package com.example.bankingprojectfinal.Exception;

public class InvalidCardActivationException extends RuntimeException {
    public InvalidCardActivationException(String message) {
        super(message);
    }
}
