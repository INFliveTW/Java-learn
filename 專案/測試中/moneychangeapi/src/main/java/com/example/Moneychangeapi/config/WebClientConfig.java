package com.example.Moneychangeapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    private final MoneyChangeapiProperties properties;

    public WebClientConfig(MoneyChangeapiProperties properties) {
        this.properties = properties;
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(properties.getApiUrl())
                .defaultHeader("app_id", properties.getApiKey())
                .build();
    }
}