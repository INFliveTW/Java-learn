package com.example.Moneychangeapi.model;

import lombok.Value;

@Value
public class ErrorMessage {
    int status;
    String message;
}