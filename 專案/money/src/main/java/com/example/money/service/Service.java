package com.example.money.service;

import com.example.money.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Service {

    private final Repository repository;

    @Autowired
    public Service(Repository repository) {
        this.repository = repository;
    }

    public String getExchangeRate(String fromCurrency, String toCurrency) {
        // 使用 repository 查詢匯率
        return repository.getExchangeRateFromDB(fromCurrency, toCurrency);
    }
}
