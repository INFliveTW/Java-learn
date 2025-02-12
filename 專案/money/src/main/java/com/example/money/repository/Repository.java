package com.example.money.repository;

import org.springframework;

@Repository
public class Repository {

    public String getExchangeRateFromDB(String fromCurrency, String toCurrency) {
        // 模擬資料庫查詢，實際應該用 JPA 查詢匯率
        return "123.45";  // 模擬匯率
    }
}
