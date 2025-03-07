package com.example.Moneychangeapi.model;

import lombok.Data;

@Data
public class ErrorMessage {
    private final int status;
    private final String message;
}
