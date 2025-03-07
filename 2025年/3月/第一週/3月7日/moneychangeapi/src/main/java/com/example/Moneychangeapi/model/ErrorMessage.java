package com.example.Moneychangeapi.model;

import lombok.Data;

@Data
public class ErrorMessage {
    private final int status;
    private final String message;

    public ErrorMessage(int value, String errorMessage) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
