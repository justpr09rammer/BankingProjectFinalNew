package com.example.bankingprojectfinal.Exception;

public class MaximumAccountCountException extends RuntimeException {
    public MaximumAccountCountException(Integer message) {
        super(String.valueOf(message));
    }
}
