package com.example.Moneychangeapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Moneychangeapi.service.ExchangeRateService;

import reactor.core.publisher.Mono;

@RestController
public class ExchangeRateController {

    private final ExchangeRateService service;

    // 建構子注入服務
    public ExchangeRateController(ExchangeRateService service) {
        this.service = service;
    }

    // 取得匯率的 API
    // @GetMapping("/exchange-rate")
    // public Mono<ResponseEntity<Map<String, Object>>> getExchangeRate(
    //         @RequestParam String base,
    //         @RequestParam String target) {

    //     return service.getExchangeRate(base, target)
    //             .map(rate -> {
    //                 Map<String, Object> response = new HashMap<>();
    //                 response.put("base", base);
    //                 response.put("target", target);
    //                 response.put("rate", rate);
    //                 return ResponseEntity.ok(response);
    //             })
    //             .onErrorResume(ex -> {
    //                 // 處理錯誤情況，回傳內部伺服器錯誤
    //                 Map<String, Object> errorResponse = new HashMap<>();
    //                 errorResponse.put("error", "伺服器錯誤");
    //                 errorResponse.put("message", ex.getMessage());
    //                 return Mono.just(ResponseEntity.internalServerError().body(errorResponse));
    //             });
    // }

    @GetMapping("/exchange-rate")
    public Mono<String> getExchangeRate(
            @RequestParam String base,
            @RequestParam String target) {

        return service.getExchangeRate(base, target)
                .map(rate -> {
                    return rate;
                })
                .onErrorResume(ex -> {
                    // 處理錯誤情況，回傳內部伺服器錯誤
                    return Mono.just("伺服器錯誤");
                });
    }
}
