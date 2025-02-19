//業務邏輯
package com.example.weather.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.weather.model.WeatherResponse;

import reactor.core.publisher.Mono;

@Service
public class WeatherService {

    private final WebClient webClient;

    public WeatherService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<WeatherResponse> getWeather(String city, String apiKey) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/weather")
                        .queryParam("q", city)
                        .queryParam("appid", apiKey)
                        .queryParam("units", "metric") // 使用攝氏度
                        .build())
                .retrieve()
                .bodyToMono(WeatherResponse.class);
    }
}
