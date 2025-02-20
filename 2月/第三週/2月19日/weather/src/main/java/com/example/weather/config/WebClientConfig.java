package com.example.weather.config;

import org.springframework.beans.factory.annotation.Value; //新增Value
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration // @Configuration 註解
public class WebClientConfig {
    
    @Value("${weather.api.base-url}") //在application.yml使用weather底下的api底下的base-url網址
    private String baseUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
            .baseUrl(baseUrl)
            .build();
}
    /*
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("https://api.openweathermap.org/data/2.5")
                .build();
    }
    */
}