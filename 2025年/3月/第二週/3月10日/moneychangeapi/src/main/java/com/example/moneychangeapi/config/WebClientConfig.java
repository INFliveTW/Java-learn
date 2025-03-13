package com.example.moneychangeapi.config;

import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.moneychangeapi.util.WebClientUtil;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import reactor.netty.http.client.HttpClient;

@Configuration //提供@Bean方法，Spring Boot會自動掃描
public class WebClientConfig {

    private final MoneyChangeapiProperties properties;

    public WebClientConfig(MoneyChangeapiProperties properties) {
        this.properties = properties;
    }

    @Bean // 標記返回物件為Bean，Spring Boot管理
    public WebClient webClient() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, properties.getConnectTimeout())
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(properties.getReadTimeout(), TimeUnit.MILLISECONDS)));

        return WebClient.builder()
                .baseUrl(properties.getApiUrl()) // 使用 https://api.currencyfreaks.com/v2.0
                .defaultHeader("Accept", "application/json")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
    @Bean
    public WebClientUtil webClientUtil(WebClient webClient) {
        return new WebClientUtil(webClient, properties.getApiKey());
}
}
// .filter((request, next) -> {
                //     return next.exchange(request).doOnNext(response -> {
                //         response.bodyToMono(String.class).subscribe(rawBody -> {
                //             System.out.println("API Raw Response (Status: " + response.statusCode() + "): " + rawBody);
                //         });
                //     });
                // })