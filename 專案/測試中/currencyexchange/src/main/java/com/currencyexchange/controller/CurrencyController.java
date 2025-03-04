package com.currencyexchange.controller;

import com.currencyexchange.model.ExchangeRate;
import com.currencyexchange.service.ExchangeRateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exchange-rates")
@Tag(name = "Currency Exchange API", description = "Endpoints for managing currency exchange rates")
public class CurrencyController {

    private final ExchangeRateService exchangeRateService;

    @Autowired
    public CurrencyController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new exchange rate")
    public ExchangeRate createExchangeRate(@RequestBody ExchangeRate exchangeRate) {
        return exchangeRateService.createExchangeRate(exchangeRate);
    }

    @GetMapping
    @Operation(summary = "List all exchange rates")
    public List<ExchangeRate> listAllExchangeRates() {
        return exchangeRateService.listAllExchangeRates();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find an exchange rate by ID")
    public ExchangeRate getExchangeRate(@PathVariable Long id) {
        return exchangeRateService.findExchangeRateById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing exchange rate")
    public ExchangeRate updateExchangeRate(@PathVariable Long id, @RequestBody ExchangeRate exchangeRate) {
        return exchangeRateService.updateExchangeRate(id, exchangeRate);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an exchange rate")
    public void deleteExchangeRate(@PathVariable Long id) {
        exchangeRateService.deleteExchangeRate(id);
    }
}