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

    public ExchangeService(ExchangeRepository exchangeRepository) {
        this.exchangeRepository = exchangeRepository;
    }

    public Map<String, Object> convert(String from, String to, double amount) {
        Map<String, Object> result = new HashMap<>();

        if (from.equalsIgnoreCase(to)) {
            result.put("from", from);
            result.put("to", to);
            result.put("amount", amount);
            result.put("convertedAmount", amount);
            return result;
        }

        // 嘗試查找 from ➝ to 的直接匯率
        Optional<ExchangeRateEntity> directRateOpt = exchangeRepository.findByFromCurrencyAndToCurrency(from, to);
        if (directRateOpt.isPresent()) {
            double rate = directRateOpt.get().getRate();
            double convertedAmount = amount * rate;

            result.put("from", from);
            result.put("to", to);
            result.put("amount", amount);
            result.put("convertedAmount", convertedAmount);
            return result;
        }

        // 改為中介方式（經由 TWD）
        Optional<ExchangeRateEntity> fromToTWDOpt = exchangeRepository.findByFromCurrencyAndToCurrency(from, "TWD");
        Optional<ExchangeRateEntity> toToTWDOpt = exchangeRepository.findByFromCurrencyAndToCurrency(to, "TWD");

        if (fromToTWDOpt.isEmpty() || toToTWDOpt.isEmpty()) {
            result.put("error", "Unsupported currency: " + from + " or " + to);
            return result;
        }

        double fromToTWD = fromToTWDOpt.get().getRate(); // 來源 ➝ 台幣
        double toToTWD = toToTWDOpt.get().getRate();     // 目標 ➝ 台幣

        // amount * 來源 ➝ TWD，再除以 目標 ➝ TWD
        double convertedAmount = (amount * fromToTWD) / toToTWD;

        result.put("from", from);
        result.put("to", to);
        result.put("amount", amount);
        result.put("convertedAmount", convertedAmount);
        return result;
    }
}
