package com.example.weather.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.weather.config.WeatherProperties;
import com.example.weather.model.WeatherResponse;

import reactor.core.publisher.Mono;

@Service
public class OpenWeatherService implements WeatherService {

    private final WebClient webClient;
    private final WeatherProperties weatherProperties;

    public OpenWeatherService(WebClient webClient, WeatherProperties weatherProperties) {
        this.webClient = webClient;
        this.weatherProperties = weatherProperties;
    }

    @Override
    public Mono<WeatherResponse> getWeather(String city) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/weather")
                        .queryParam("q", city)
                        .queryParam("appid", weatherProperties.getKey()) // 從 WeatherProperties 取 API key
                        .queryParam("units", "metric") // 使用攝氏度
                        .build())
                .retrieve()
                .bodyToMono(WeatherResponse.class);
    }
}
