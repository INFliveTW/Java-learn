package com.example.Moneychangeapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Value;

@Configuration
@ConfigurationProperties(prefix = "moneychangeapi")
@Value
public class MoneyChangeapiProperties {
    String apiUrl;
    String apiKey;
    int connectTimeout;
    int readTimeout;
}