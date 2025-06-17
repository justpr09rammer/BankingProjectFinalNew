package com.example.bankingprojectfinal.Exception;

public class InvalidAccountActivationException extends RuntimeException {
    public InvalidAccountActivationException(String message) {
        super(message);
    }
}
