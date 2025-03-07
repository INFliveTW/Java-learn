package com.example.Moneychangeapi.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.Moneychangeapi.config.MoneyChangeapiProperties;

import reactor.core.publisher.Mono;

@Service
public class ExchangeRateService {
    
    private final WebClient webClient;
    private final MoneyChangeapiProperties properties;

    public ExchangeRateService(WebClient webClient, MoneyChangeapiProperties properties) {
        this.webClient = webClient;
        this.properties = properties;
    }

    public Mono<String> getExchangeRate(String base, String target) {
        // String apiUrl = "/latest?&target=" + target + "?base=" + base;
        //String apiUrl = "/latest?&base=" + base + "?target=" + target;
        System.err.println("key = " + properties.getApiKey());
        System.err.println(properties.getApiUrl() +"/latest.json?app_id=" + properties.getApiKey() + "&currency=" + target + "&base_currency=" + base);
        return webClient.get()
                //.uri("&currencies={target}&base_currency={base}", target, base)
                //.uri("&currencies={target}&base_currency={base}", target, base)
                //.uri("?apikey=" + properties.getApiKey() + "&currency=" + target + "base_currency" + base)
                // .uri(properties.getApiUrl() +"/latest?currency=" + "CAD" + "base_currency" + "JPY")
                .uri(properties.getApiUrl() +"/latest.json?app_id=" + properties.getApiKey() + "&currency=" + target + "&base_currency=" + base)
                .header("apikey", properties.getApiKey())
                .retrieve()
                // .bodyToMono(MoneychangeapiResponse.class)
                .bodyToMono(String.class)
                // .map(response -> {
                //     reture response.toString();
                //     // if (response.getConversionRates() != null && response.getConversionRates().containsKey(target)) {
                //     //     return response.getConversionRates().get(target);
                //     // } else {
                //     //     throw new RuntimeException("無法獲取貨幣匯率");
                //     // }
                // })
                .onErrorResume(ex -> Mono.error(new RuntimeException("API 呼叫失敗: " + ex.getMessage())));
    }
}
