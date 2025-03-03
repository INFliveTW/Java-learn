package com.example.weather.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.weather.model.WeatherResponse;
import com.example.weather.service.WeatherService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/weather")
public class WeatherController {

    private final WeatherService weatherService;

    // 使用建構子注入 WeatherService，這樣可以更容易更換不同的實作（如 OpenWeatherService）
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/{city}")
    public Mono<WeatherResponse> getWeather(@PathVariable String city) {
        return weatherService.getWeather(city);
    }
}