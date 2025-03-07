package com.example.Moneychangeapi.model;

import java.util.Map;

public class MoneychangeapiResponse {
    private String base;
    private Map<String, Double> conversion_rates;

    public String getBase() {
        return base;
    }

    public Map<String, Double> getConversionRates() {
        return conversion_rates;
    }

    @Override
    public String toString() {
        return "MoneychangeapiResponse [base=" + base + ", conversion_rates=" + conversion_rates + "]";
    }
}
