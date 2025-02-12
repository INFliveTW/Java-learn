package com.example.money.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.money.service.Service;

@RestController
public class Controller {

    private final Service service;

    @Autowired
    public Controller(Service service) {
        this.service = service;
    }

    @GetMapping("/exchange-rate")
    public String getExchangeRate(@RequestParam String fromCurrency, @RequestParam String toCurrency) {
        return service.getExchangeRate(fromCurrency, toCurrency);
    }
}
