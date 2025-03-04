package com.currencyexchange.service;

import com.currencyexchange.exception.ExchangeRateNotFoundException;
import com.currencyexchange.model.ExchangeRate;
import com.currencyexchange.repository.ExchangeRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;

    @Autowired
    public ExchangeRateService(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
    }

    public ExchangeRate createExchangeRate(ExchangeRate exchangeRate) {
        if (exchangeRate.getFromCurrency() == null || exchangeRate.getFromCurrency().trim().isEmpty() ||
            exchangeRate.getToCurrency() == null || exchangeRate.getToCurrency().trim().isEmpty() ||
            exchangeRate.getRate() == null || exchangeRate.getRate() <= 0) {
            throw new IllegalArgumentException("Invalid exchange rate data: currencies and rate must be valid and non-empty");
        }
        return exchangeRateRepository.save(exchangeRate);
    }

    public List<ExchangeRate> listAllExchangeRates() {
        return exchangeRateRepository.findAll();
    }

    public ExchangeRate findExchangeRateById(Long id) {
        return exchangeRateRepository.findById(id)
                .orElseThrow(() -> new ExchangeRateNotFoundException("No exchange rate found with ID: " + id));
    }

    public ExchangeRate updateExchangeRate(Long id, ExchangeRate updatedRate) {
        ExchangeRate existingRate = findExchangeRateById(id);
        ExchangeRate rateToSave = new ExchangeRate(existingRate.getId(), updatedRate.getFromCurrency(), 
                                                 updatedRate.getToCurrency(), updatedRate.getRate());
        return exchangeRateRepository.save(rateToSave);
    }

    public void deleteExchangeRate(Long id) {
        ExchangeRate rate = findExchangeRateById(id);
        exchangeRateRepository.delete(rate);
    }
}