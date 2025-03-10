package com.example.moneychangeapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration //提供@Bean方法，Spring Boot會自動掃描
public class WebClientConfig {

    private final MoneyChangeapiProperties properties;

    public WebClientConfig(MoneyChangeapiProperties properties) {
        this.properties = properties;
    }

    @Bean // 標記返回物件為Bean，Spring Boot管理
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(properties.getApiUrl()) // 使用 https://api.currencyfreaks.com/v2.0
                .defaultHeader("Accept", "application/json")
                // .filter((request, next) -> {
                //     return next.exchange(request).doOnNext(response -> {
                //         response.bodyToMono(String.class).subscribe(rawBody -> {
                //             System.out.println("API Raw Response (Status: " + response.statusCode() + "): " + rawBody);
                //         });
                //     });
                // })
                .build();
    }
}

