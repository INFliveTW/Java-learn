package com.example.moneychangeapi.service;

import org.slf4j.Logger; // 導入 SLF4J 的日誌介面，用於記錄日誌。
import org.slf4j.LoggerFactory; // 導入日誌工廠，用於創建 Logger 實例。
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.moneychangeapi.config.MoneyChangeapiProperties;
import com.fasterxml.jackson.databind.JsonNode;

import reactor.core.publisher.Mono;

@Service
public class ExchangeRateService {

    private static final Logger logger = LoggerFactory.getLogger(ExchangeRateService.class);
    //記錄錯誤時使用
    //Web用於呼叫API
    private final WebClient webClient;
    private final MoneyChangeapiProperties properties;
    //Money...提供apikey配置
    public ExchangeRateService(WebClient webClient, MoneyChangeapiProperties properties) {
        this.webClient = webClient;
        this.properties = properties;
    }
    //透過建構子注入@Autowired
    //與 WebClientConfig 和 MoneyChangeapiProperties 的 @Bean 和 @Component關聯
    
    public Mono<Double> getExchangeRate(String fromCurrency, String toCurrency, double amount) {
        //公開方式，讓Controller呼叫、Mono返回轉換金額
        //被 ExchangeRateController 的 getExchangeRate 呼叫

        return webClient.get()
                .uri(uriBuilder -> {
                    //.url() Lambda表達式，queryParam 相關
                    java.net.URI uri = uriBuilder
                    //提供最終api的url
                            .path("/convert/latest")
                            //與baseUrl組合
                            //添加鍵值 URL ?以後 key=value 格式 ，分隔以&為使用
                            //FORM=USD&TO=EUR&AMOUNT=1
                            //來源貨幣=USD，目標貨幣=EUR，金額=1(1USD > ? EUR)
                            .queryParam("apikey", properties.getApiKey())
                            .queryParam("from", fromCurrency)
                            .queryParam("to", toCurrency)
                            .queryParam("amount", amount)
                            .build();
                    logger.info("API Request URL: {}", uri.toString());
                    return uri; //將url返回給.uri()
                })
                .retrieve() //觸發API呼叫
                .bodyToMono(JsonNode.class) // 原有邏輯不變
                .map(json -> { // 原有邏輯不變
                    logger.info("API Response: {}", json.toString());
                    return json.get("convertedAmount").asDouble();
                });
                    //     JsonNode convertedAmountNode = json.get("convertedAmount");
                //     if (convertedAmountNode == null || convertedAmountNode.isNull()) {
                //         throw new RuntimeException("API 回應無效，無法獲取轉換結果: " + json.toString());
                //     }
                //     return convertedAmountNode.asDouble();
                // }) // 處理 API 回應錯誤
                // .onErrorResume(WebClientResponseException.class, ex -> {
                //     logger.error("API Error: {}", ex.getResponseBodyAsString());
                //     return Mono.error(new RuntimeException("API 呼叫失敗: " + ex.getResponseBodyAsString()));
                // })
                // .onErrorMap(ex -> new RuntimeException("API 處理失敗: " + ex.getMessage()));
    }
}