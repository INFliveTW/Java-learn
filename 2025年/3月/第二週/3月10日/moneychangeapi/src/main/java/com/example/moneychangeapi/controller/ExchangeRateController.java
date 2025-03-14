package com.example.moneychangeapi.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.moneychangeapi.service.ExchangeRateService;

import reactor.core.publisher.Mono;

@RestController
public class ExchangeRateController {

    private final ExchangeRateService service;

    public ExchangeRateController(ExchangeRateService service) {
        this.service = service;
    }

    @GetMapping("/exchange-rate")
    public Mono<String> getExchangeRate( //來源貨幣FROMCURRENCYCODE、目標貨幣TOCURRENCYCODE、轉換金額(來源>目標)AMOUNT
            @RequestParam(name = "FROM") String fromCurrency,
            @RequestParam(name = "TO") String toCurrency,
            @RequestParam(name = "AMOUNT", defaultValue = "1") double amount) {
        // return service.getExchangeRate(fromCurrency, toCurrency, amount)
        //         //.map(result -> String.format("從(%s)幣別 %.2f$ = 轉換為(%s)幣別 %.2f$", fromCurrency, amount, toCurrency, result));
        //         .map(result -> {
        //             if (result.getStatus() == 200) {
        //                 double convertedAmount = Double.parseDouble(result.getMessage());
        //                 return String.format("從(%s)幣別 %.2f$ = 轉換為(%s)幣別 %.2f$", 
        //                         fromCurrency, amount, toCurrency, convertedAmount);
        //             } else {
        //                 return result.getMessage(); // 返回自訂的錯誤訊息
        //             }
        //         });
        return  null;
    }
}
//http://localhost:8080/exchange-rate?FROM=jpy&TO=twd&AMOUNT=1000
//@Controller 和 @ResponseBody，表示類處理 REST 請求並返回資料
