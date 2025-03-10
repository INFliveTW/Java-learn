package com.example.Moneychangeapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Moneychangeapi.service.ExchangeRateService;

import reactor.core.publisher.Mono;

@RestController
public class ExchangeRateController {

    private final ExchangeRateService service;

    public ExchangeRateController(ExchangeRateService service) {
        this.service = service;
    }

    @GetMapping("/exchange-rate")
    public Mono<String> getExchangeRate(
            @RequestParam String base,
            @RequestParam String target) {
        return service.getExchangeRate(base, target)
                .map(rate -> String.format("1 %s = %.2f %s", base, rate, target));
    }
}