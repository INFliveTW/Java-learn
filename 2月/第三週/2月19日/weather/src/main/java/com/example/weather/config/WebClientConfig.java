package com.example.weather.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    private final WeatherProperties weatherProperties;

    public WebClientConfig(WeatherProperties weatherProperties) {
        this.weatherProperties = weatherProperties;
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(weatherProperties.getBaseUrl()) // ✅ 確保 baseUrl 設定正確
                .build();
    }
}
