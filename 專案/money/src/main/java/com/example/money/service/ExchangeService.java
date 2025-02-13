package com.example.money.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.money.model.ExchangeRateEntity;
import com.example.money.repository.ExchangeRepository;

@Service
public class ExchangeService {

    private final ExchangeRepository exchangeRepository;

    // 構造函數注入
    public ExchangeService(ExchangeRepository exchangeRepository) {
        this.exchangeRepository = exchangeRepository;
    }

    public Map<String, Object> convert(String from, String to, double amount) {
        Map<String, Object> result = new HashMap<>();

        // 查詢來自資料庫的匯率
        Optional<ExchangeRateEntity> fromRateEntityOpt = exchangeRepository.findByFromCurrencyAndToCurrency(from, "TWD");
        Optional<ExchangeRateEntity> toRateEntityOpt = exchangeRepository.findByFromCurrencyAndToCurrency(to, "TWD");

        if (fromRateEntityOpt.isEmpty() || toRateEntityOpt.isEmpty()) {
            result.put("error", "Unsupported currency");
            return result;
        }

        ExchangeRateEntity fromRateEntity = fromRateEntityOpt.get();
        ExchangeRateEntity toRateEntity = toRateEntityOpt.get();

        double rateFromTWD = fromRateEntity.getRate();
        double rateToTWD = toRateEntity.getRate();
        double convertedAmount = (amount / rateFromTWD) * rateToTWD;

        result.put("from", from);
        result.put("to", to);
        result.put("amount", amount);
        result.put("convertedAmount", convertedAmount);
        return result;
    }
}
