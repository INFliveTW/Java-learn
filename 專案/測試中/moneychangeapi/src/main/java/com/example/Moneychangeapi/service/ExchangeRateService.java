package com.example.Moneychangeapi.service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.Moneychangeapi.config.ExchangeRateRepository;
import com.example.Moneychangeapi.config.MoneyChangeapiProperties; // 明確導入 Map
import com.example.Moneychangeapi.model.MoneychangeapiResponse;

import reactor.core.publisher.Mono;

@Service
public class ExchangeRateService {

    private final WebClient webClient;
    private final MoneyChangeapiProperties properties;
    private final ExchangeRateRepository repository;

    public ExchangeRateService(WebClient webClient, MoneyChangeapiProperties properties, ExchangeRateRepository repository) {
        this.webClient = webClient;
        this.properties = properties;
        this.repository = repository;
    }

    public Mono<Double> getExchangeRate(String base, String target) {
        return fetchRates()
                .flatMap(rates -> {
                    if (!repository.containsCurrency(base)) {
                        throw new IllegalArgumentException(base + " 找不到幣值，請輸入正確幣值，支援幣別：" + getSupportedCurrencies());
                    }
                    if (!repository.containsCurrency(target)) {
                        throw new IllegalArgumentException(target + " 找不到幣值，請輸入正確幣值，支援幣別：" + getSupportedCurrencies());
                    }
                    if (!base.equals("USD") && !target.equals("USD")) {
                        if (!repository.containsCurrency(base) || !repository.containsCurrency(target)) {
                            throw new IllegalArgumentException(base + " & " + target + " 均找不到幣值，請輸入正確幣值，支援幣別：" + getSupportedCurrencies());
                        }
                    }

                    return Mono.just(calculateRate(base, target));
                });
    }

    private Mono<Map<String, Double>> fetchRates() {
        return webClient.get()
                .uri("/latest.json")
                .retrieve()
                .bodyToMono(MoneychangeapiResponse.class)
                .map(MoneychangeapiResponse::getRates)
                .doOnNext(repository::saveRates)
                .onErrorResume(ex -> Mono.error(new RuntimeException("API 呼叫失敗: " + ex.getMessage())));
    }

    private double calculateRate(String base, String target) {
        if (base.equals("USD") && target.equals("USD")) {
            return 1.0;
        } else if (base.equals("USD")) {
            return repository.getRate(target);
        } else if (target.equals("USD")) {
            return 1.0 / repository.getRate(base);
        } else {
            return repository.getRate(target) / repository.getRate(base);
        }
    }

    private String getSupportedCurrencies() {
        Set<String> currencies = repository.getAllCurrencies();
        currencies.add("USD");
        return currencies.stream().sorted().collect(Collectors.joining(", "));
    }
}