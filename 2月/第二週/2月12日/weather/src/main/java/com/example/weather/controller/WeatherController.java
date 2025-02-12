package com.example.weather.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.weather.entity.Weather;
import com.example.weather.service.WeatherService;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    // 取得即時天氣
    @GetMapping("/current")
    public Weather getWeather(@RequestParam String city) {
        return weatherService.getWeather(city);
    }

    // 取得歷史天氣記錄
    @GetMapping("/history")
    public List<Weather> getWeatherHistory(@RequestParam String city) {
        return weatherService.getWeatherHistory(city);
    }
}