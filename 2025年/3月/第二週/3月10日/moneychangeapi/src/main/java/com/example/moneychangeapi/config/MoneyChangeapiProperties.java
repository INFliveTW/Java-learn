package com.example.moneychangeapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data // Lombok 提供的 @Data 注解，自動生成 getter/setter 等方法
@Component // 添加此注解使其成為 Spring Bean
@ConfigurationProperties(prefix = "moneychangeapi")
public class MoneyChangeapiProperties {
    private String apiUrl;
    private String apiKey;
    private int connectTimeout;
    private int readTimeout;
}