package com.example.Moneychangeapi.exception;
 //超時例外
public class ApiTimeoutException extends RuntimeException {
    public ApiTimeoutException(String message) {
        super(message);
    }
}
