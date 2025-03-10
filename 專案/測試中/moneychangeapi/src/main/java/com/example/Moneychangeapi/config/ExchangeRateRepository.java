package com.example.Moneychangeapi.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class ExchangeRateRepository {
    private final Map<String, Double> rates = new HashMap<>();

    public void saveRates(Map<String, Double> newRates) {
        rates.clear();
        rates.putAll(newRates);
    }

    public Double getRate(String currency) {
        return rates.get(currency);
    }

    public boolean containsCurrency(String currency) {
        return rates.containsKey(currency) || "USD".equals(currency);
    }

    public Set<String> getAllCurrencies() {
        return rates.keySet();
    }
}