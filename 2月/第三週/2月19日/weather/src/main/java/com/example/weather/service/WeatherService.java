package com.example.weather.service;

import com.example.weather.model.WeatherResponse;

import reactor.core.publisher.Mono;

public interface WeatherService {
    Mono<WeatherResponse> getWeather(String city);
}
