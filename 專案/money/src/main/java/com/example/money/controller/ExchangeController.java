package com.example.money.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.money.service.ExchangeService;

@RestController
@RequestMapping("/api") // 定義 API 路徑前綴為 /api
public class ExchangeController {
    private final ExchangeService exchangeService;

    // 透過建構子注入 Service
    public ExchangeController(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    @GetMapping("/convert")
    public Map<String, Object> convertCurrency(
        @RequestParam String from, // 來源貨幣
        @RequestParam String to,   // 目標貨幣
        @RequestParam double amount // 轉換金額
    ) {
        return exchangeService.convert(from, to, amount);
    }
}
