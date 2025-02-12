package com.example.weather.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.weather.entity.Weather;
import com.example.weather.repository.WeatherRepository;

@Service
public class WeatherService {

    private final WeatherRepository weatherRepository;
    private final RestTemplate restTemplate;

    @Value("${weather.api.url}")
    private String weatherApiUrl;

    public WeatherService(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
        this.restTemplate = new RestTemplate();
    }

    public Weather getWeather(String city) {
        String url = weatherApiUrl + "?latitude=25.03&longitude=121.56&current_weather=true";

        // 取得外部 API 數據
        var response = restTemplate.getForObject(url, WeatherResponse.class);

        if (response != null && response.current_weather != null) {
            Weather weather = new Weather();
            weather.setCity(city);
            weather.setTemperature(response.current_weather.temperature);
            weather.setDescription("Sunny"); // 可使用更精確的天氣描述
            weather.setTimestamp(LocalDateTime.now());

            return weatherRepository.save(weather);
        }

        return null;
    }

    public List<Weather> getWeatherHistory(String city) {
        return weatherRepository.findByCity(city);
    }

    // 用於解析 API 回應
    private static class WeatherResponse {
        public CurrentWeather current_weather;
    }

    private static class CurrentWeather {
        public Double temperature;
    }
}
