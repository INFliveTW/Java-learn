package com.example.weather.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
@ConfigurationProperties(prefix = "weather.api")
public class WeatherProperties {
    private String key;
    private String baseUrl;

    // ✅ 確保 key 和 baseUrl 讀取成功
    @PostConstruct
    public void init() {
        System.out.println("API Key: " + key);
        System.out.println("Base URL: " + baseUrl);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
