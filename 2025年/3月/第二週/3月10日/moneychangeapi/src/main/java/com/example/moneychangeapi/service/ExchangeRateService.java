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
    }
}
//                     //     JsonNode convertedAmountNode = json.get("convertedAmount");
//                 //     if (convertedAmountNode == null || convertedAmountNode.isNull()) {
//                 //         throw new RuntimeException("API 回應無效，無法獲取轉換結果: " + json.toString());
//                 //     }
//                 //     return convertedAmountNode.asDouble();
//                 // }) // 處理 API 回應錯誤
//                 // .onErrorResume(WebClientResponseException.class, ex -> {
//                 //     logger.error("API Error: {}", ex.getResponseBodyAsString());
//                 //     return Mono.error(new RuntimeException("API 呼叫失敗: " + ex.getResponseBodyAsString()));
//                 // })
//                 // .onErrorMap(ex -> new RuntimeException("API 處理失敗: " + ex.getMessage()));

// package com.example.moneychangeapi.service;

// import java.time.Duration;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.stereotype.Service;
// import org.springframework.web.reactive.function.client.WebClient;
// import org.springframework.web.reactive.function.client.WebClientResponseException;

// import com.example.moneychangeapi.config.MoneyChangeapiProperties;
// import com.example.moneychangeapi.exception.ApiTimeoutException;
// import com.fasterxml.jackson.databind.JsonNode;

// import reactor.core.publisher.Mono;

// @Service
// public class ExchangeRateService {

//     private static final Logger logger = LoggerFactory.getLogger(ExchangeRateService.class);
//     private final WebClient webClient;
//     private final MoneyChangeapiProperties properties;

//     public ExchangeRateService(WebClient webClient, MoneyChangeapiProperties properties) {
//         this.webClient = webClient;
//         this.properties = properties;
//     }

//     public Mono<Double> getExchangeRate(String fromCurrency, String toCurrency, double amount) {
//         // 檢查幣值是否為英文
//         if (!fromCurrency.matches("^[A-Za-z]+$") || !toCurrency.matches("^[A-Za-z]+$")) {
//             String errorMessage = "輸入幣別非英文，請填寫正確以開始匯率轉換";
//             logger.error("輸入幣別非英文: from={}, to={}", fromCurrency, toCurrency);
//             System.out.println(errorMessage);
//             return Mono.empty(); // 返回空的 Mono 表示流程結束
//         }

//         // 構建 API 請求
//         return Mono.fromCallable(() -> {
//             String errorMessage;
//             try {
//                 JsonNode json = webClient.get()
//                         .uri(uriBuilder -> {
//                             java.net.URI uri = uriBuilder
//                                     .path("/convert/latest")
//                                     .queryParam("apikey", properties.getApiKey())
//                                     .queryParam("from", fromCurrency)
//                                     .queryParam("to", toCurrency)
//                                     .queryParam("amount", amount)
//                                     .build();
//                             logger.info("API Request URL: {}", uri.toString());
//                             return uri;
//                         })
//                         .retrieve()
//                         .bodyToMono(JsonNode.class)
//                         .timeout(Duration.ofSeconds(5)) // 設置 5 秒超時
//                         .onErrorMap(
//                             Throwable.class, // 捕獲所有異常
//                             e -> new ApiTimeoutException("API 請求超時，超過 5 秒") // 直接映射為 ApiTimeoutException
//                         )
//                         .block(); // 同步執行以使用 try-catch

//                 // 無論是否有錯，都記錄回應
//                 logger.info("API Response: {}", json.toString());

//                 // 直接返回 convertedAmount，不額外檢查 null 或 isNull
//                 return json.get("convertedAmount").asDouble();

//             } catch (WebClientResponseException ex) {
//                 int status = ex.getRawStatusCode();
//                 String responseBody = ex.getResponseBodyAsString();
//                 logger.error("API Error - Status: {}, Response: {}", status, responseBody);
//                 // 無論是否有錯，都記錄回應
//                 logger.info("API Response: {}", responseBody);

//                 if (status == 400) {
//                     errorMessage = "無效金額/URL無效";
//                     System.out.println(errorMessage);
//                     return null;
//                 }
//                 if (status == 401) {
//                     errorMessage = "API KEY無效/非活躍";
//                     System.out.println(errorMessage);
//                     return null;
//                 }
//                 if (status == 404) {
//                     errorMessage = "找不到有效的URL";
//                     System.out.println(errorMessage);
//                     return null;
//                 }
//                 if (status == 500) {
//                     errorMessage = "伺服器內部錯誤，請聯繫管理員";
//                     System.out.println(errorMessage);
//                     return null;
//                 }
//                 // 未特別處理的狀態碼也列印回應
//                 errorMessage = "API 錯誤: " + status + ", 回應: " + responseBody;
//                 System.out.println(errorMessage);
//                 return null;

//            // } catch (java.util.concurrent.TimeoutException e) {
//             }  catch (ApiTimeoutException e) {
//                 logger.error("API Timeout: {}", e.getMessage());
//                 errorMessage = "API 請求超時，超過 5 秒";
//                 System.out.println(errorMessage);
//                 return null;
//             } catch (Exception e) {
//                 logger.error("Unexpected error: {}", e.getMessage(), e);
//                 errorMessage = "伺服器內部錯誤，請聯繫管理員";
//                 System.out.println(errorMessage);
//                 return null;
//             }
//         });
//     }
// }