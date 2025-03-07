package com.example.Moneychangeapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration //這是一個配置類
public class WebClientConfig {

    private final MoneyChangeapiProperties moneychangeapiProperties;

    public WebClientConfig(MoneyChangeapiProperties moneychangeapiProperties) {
        this.moneychangeapiProperties = moneychangeapiProperties;
    }
    
     @Bean
    public WebClient webClient() {
        System.out.println(moneychangeapiProperties.getApiUrl() +"/latest?apikey=" + moneychangeapiProperties.getApiKey());
        return WebClient.builder()
                // .baseUrl(moneychangeapiProperties.getApiUrl() +"/latest?apikey=fca_live_RNZT1IxqlN5F0qnROpZk2SfjmLlXGIWHTB6Gw5Y8&currency=" + "CAD" + "base_currency" + "JPY") 
                .build();
    // @Bean
    // public WebClient webClient(MoneyChangeapiProperties moneychangeapiproperties) {
    //     return WebClient.builder()
    //             .baseUrl(properties.getApiUrl())
    //             .defaultHeader("apikey", properties.getApiKey())
    //             .build();
    // }
}
}