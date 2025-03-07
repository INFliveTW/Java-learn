package com.example.weatherforecast.model;
//統一處理錯誤
import lombok.Data;

@Data
public class ErrorMessage {
    private int status;      // HTTP 狀態碼，例如 400、404、500
    private String message;  // 錯誤訊息，例如 "城市名稱輸入錯誤，請輸入正確城市名"

    public ErrorMessage(int status, String message) {
        this.status = status;
        this.message = message;
    }
}