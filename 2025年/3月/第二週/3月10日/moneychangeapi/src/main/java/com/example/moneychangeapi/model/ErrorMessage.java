package com.example.moneychangeapi.model;

import lombok.Data;

@Data
public class ErrorMessage {
    private int status;
    private String message;
    public ErrorMessage(int status, String message) {
        this.status = status; //this... = private int status; 後 = int ...
        this.message = message;
    }
    // public ErrorMessage(int value, String errorMessage) {
    //     throw new UnsupportedOperationException("Not supported yet.");
    // }
}
