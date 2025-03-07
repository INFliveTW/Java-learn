package com.example.Moneychangeapi.exception;

public class ApiTimeoutException extends RuntimeException {
    public ApiTimeoutException(String message) {
        super(message);
    }
}
