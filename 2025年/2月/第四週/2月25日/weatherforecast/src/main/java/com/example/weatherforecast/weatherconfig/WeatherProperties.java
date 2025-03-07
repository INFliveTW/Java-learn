package com.example.weatherforecast.weatherconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "weather.api")
@Data
public class WeatherProperties {
    // 宣告一個私有變數 key，用來儲存 API 金鑰。
    private String key;
    // 宣告一個私有變數 baseUrl，用來儲存 API 的基礎網址。
    private String baseUrl;
    @PostConstruct
    public void init() {
        System.out.println("API Key: " + key);
        System.out.println("Base URL: " + baseUrl);
    }
}