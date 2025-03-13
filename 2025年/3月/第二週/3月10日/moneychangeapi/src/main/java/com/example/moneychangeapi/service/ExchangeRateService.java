package com.example.moneychangeapi.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.example.moneychangeapi.config.MoneyChangeapiProperties;
import com.example.moneychangeapi.model.ErrorMessage;
import com.example.moneychangeapi.util.WebClientUtil;
import com.fasterxml.jackson.databind.JsonNode;

import reactor.core.publisher.Mono;

@Service
public class ExchangeRateService {

    private static final Logger logger = LoggerFactory.getLogger(ExchangeRateService.class);
    private final WebClient webClient;
    private final MoneyChangeapiProperties properties;
    private final WebClientUtil webClientUtil;

    public ExchangeRateService(WebClient webClient, MoneyChangeapiProperties properties, WebClientUtil webClientUtil) {
        this.webClient = webClient;
        this.properties = properties;
        this.webClientUtil = webClientUtil;
    }

    private boolean isAllLetters(String word) {
        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            logger.debug("Checking char: {} (isLetter: {})", ch, Character.isLetter(ch));
            if (Character.UnicodeBlock.of(ch) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) {
                return false;
            }
            if (!((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z'))) {
                return false;
            }
        }
        return true;
    }

    private Mono<List<String>> getSupportedCurrencies() {
        return webClient.get()
                .uri("https://api.currencyfreaks.com/v2.0/currency-symbols")
                .retrieve()
                .bodyToMono(JsonNode.class)
                //.timeout(Duration.ofMillis(100))
                .map(json -> {
                    JsonNode currencySymbols = json.get("currencySymbols");
                    List<String> supportedCurrencies = new ArrayList<>();
                    currencySymbols.fieldNames().forEachRemaining(supportedCurrencies::add);
                    return supportedCurrencies;
                })
                .onErrorResume(e -> {
                    logger.error("not support: {}", e.getMessage());
                    return Mono.error(new RuntimeException("API 請求失敗: " + e.getMessage()));
                });
    }

    // 定義貨幣轉換的請求參數物件
    private static class CurrencyRequest {
        String fromCurrency;
        String toCurrency;
        double amount;

        CurrencyRequest(String fromCurrency, String toCurrency, double amount) {
            this.fromCurrency = fromCurrency;
            this.toCurrency = toCurrency;
            this.amount = amount;
        }
    }

    public Mono<ErrorMessage> getExchangeRate(String fromCurrency, String toCurrency, double amount) {
        // 提取兩個空值
        String fromError = " ";
        String toError = " ";
    
        // 檢查 from 是否為英文
        if (!isAllLetters(fromCurrency)) {
            fromError = fromCurrency;
            logger.error("輸入字元中有非英文: from={}", fromCurrency);
        }
    
        // 檢查 to 是否為英文
        if (!isAllLetters(toCurrency)) {
            toError = toCurrency;
            logger.error("輸入字元中有非英文: to={}", toCurrency);
        }
    
        // 直接根據空值判斷錯誤訊息
        if (!fromError.equals(" ") && !toError.equals(" ")) {
            String errorMessage = String.format("\"%s\" \"%s\" FROM & TO 貨幣代碼必須均為英文字母", fromCurrency, toCurrency);
            logger.error("輸入字元中有非英文: from={}, to={}", fromCurrency, toCurrency);
            return Mono.just(new ErrorMessage(400, errorMessage));
        }
        if (!fromError.equals(" ") && toError.equals(" ")) {
            String errorMessage = String.format("\"%s\" FROM貨幣代碼必須為英文字母", fromCurrency);
            logger.error("輸入字元中有非英文: from={}", fromCurrency);
            return Mono.just(new ErrorMessage(400, errorMessage));
        }
        if (fromError.equals(" ") && !toError.equals(" ")) {
            String errorMessage = String.format("\"%s\" TO貨幣代碼必須為英文字母", toCurrency);
            logger.error("輸入字元中有非英文: from={}", toCurrency);
            return Mono.just(new ErrorMessage(400, errorMessage));
        }

    // public Mono<ErrorMessage> getExchangeRate(String fromCurrency, String toCurrency, double amount) {
    //     if (!isAllLetters(fromCurrency)) {
    //         String errorMessage = String.format("\"%s\" FROM貨幣代碼必須為英文字母", fromCurrency);
    //         logger.error("輸入字元中有非英文: from={}", fromCurrency);
    //         return Mono.just(new ErrorMessage(400, errorMessage));
    //     }
    //     if (!isAllLetters(toCurrency)) {
    //         String errorMessage = String.format("\"%s\" TO貨幣代碼必須為英文字母", toCurrency);
    //         logger.error("輸入字元中有非英文: to={}", toCurrency);
    //         return Mono.just(new ErrorMessage(400, errorMessage));
    //     }
    //     if (!isAllLetters(fromCurrency) && !isAllLetters(toCurrency)) {
    //         String errorMessage = String.format("\"%s\" \"%s\" FT貨幣代碼必須為英文字母", fromCurrency, toCurrency);
    //         logger.error("輸入字元中有非英文: from={}, to={}", fromCurrency, toCurrency);
    //         return Mono.just(new ErrorMessage(400, errorMessage));
    //     }

    // 檢查貨幣是否存在
    return getSupportedCurrencies()
                .flatMap(supportedCurrencies -> {
                    String fromExistsError = " ";
                    String toExistsError = " ";
                    String supportedCurrenciesStr = String.join("\", \"", supportedCurrencies);

                    if (!supportedCurrencies.contains(fromCurrency.toUpperCase())) {
                        fromExistsError = fromCurrency;
                        logger.error("貨幣不存在: from={}", fromCurrency);
                    }
                    if (!supportedCurrencies.contains(toCurrency.toUpperCase())) {
                        toExistsError = toCurrency;
                        logger.error("貨幣不存在: to={}", toCurrency);
                    }

                    if (!fromExistsError.equals(" ") && !toExistsError.equals(" ")) {
                        String errorMessage = String.format("\"%s\" \"%s\" FROM & TO 貨幣符號均不存在，請確認貨幣再重新嘗試\n支援的貨幣符號: \"%s\"",
                                fromExistsError, toExistsError, supportedCurrenciesStr);
                        logger.error("貨幣不存在: from={}, to={}", fromExistsError, toExistsError);
                        return Mono.just(new ErrorMessage(400, errorMessage));
                    }
                    if (!fromExistsError.equals(" ") && toExistsError.equals(" ")) {
                        String errorMessage = String.format("\"%s\" FROM貨幣符號不存在，請確認貨幣再重新嘗試\n支援的貨幣符號: \"%s\"",
                                fromExistsError, supportedCurrenciesStr);
                        logger.error("貨幣不存在: from={}", fromExistsError);
                        return Mono.just(new ErrorMessage(400, errorMessage));
                    }
                    if (fromExistsError.equals(" ") && !toExistsError.equals(" ")) {
                        String errorMessage = String.format("\"%s\" TO貨幣符號不存在，請確認貨幣再重新嘗試\n支援的貨幣符號: \"%s\"",
                                toExistsError, supportedCurrenciesStr);
                        logger.error("貨幣不存在: to={}", toExistsError);
                        return Mono.just(new ErrorMessage(400, errorMessage));
                    }
        // return getSupportedCurrencies()
        //         .flatMap(supportedCurrencies -> {
        //             boolean fromExists = supportedCurrencies.contains(fromCurrency.toUpperCase());
        //             boolean toExists = supportedCurrencies.contains(toCurrency.toUpperCase());
        //             String supportedCurrenciesStr = String.join("\", \"", supportedCurrencies);

        //             if (!fromExists && toExists) {
        //                 String errorMessage = String.format("\"%s\" 貨幣符號不存在，請確認貨幣再重新嘗試\n支援的貨幣符號: \"%s\"",
        //                         fromCurrency, supportedCurrenciesStr);
        //                 logger.error("貨幣不存在: from={}", fromCurrency);
        //                 return Mono.just(new ErrorMessage(400, errorMessage));
        //             }
        //             if (fromExists && !toExists) {
        //                 String errorMessage = String.format("\"%s\" 貨幣符號不存在，請確認貨幣再重新嘗試\n支援的貨幣符號: \"%s\"",
        //                         toCurrency, supportedCurrenciesStr);
        //                 logger.error("貨幣不存在: to={}", toCurrency);
        //                 return Mono.just(new ErrorMessage(400, errorMessage));
        //             }
        //             if (!fromExists && !toExists) {
        //                 String errorMessage = String.format("\"%s\" \"%s\" 貨幣符號均不存在，請確認貨幣再重新嘗試\n支援的貨幣符號: \"%s\"",
        //                         fromCurrency, toCurrency, supportedCurrenciesStr);
        //                 logger.error("貨幣不存在: from={}, to={}", fromCurrency, toCurrency);
        //                 return Mono.just(new ErrorMessage(400, errorMessage));
        //             }

                    try {
                        CurrencyRequest request = new CurrencyRequest(fromCurrency, toCurrency, amount);
                        return webClientUtil.buildRequest(
                                "/convert/latest", // String url
                                request,           // Object request
                                uriBuilder -> {    // 配置查詢參數
                                    CurrencyRequest req = (CurrencyRequest) request;
                                    uriBuilder
                                            .queryParam("from", req.fromCurrency)
                                            .queryParam("to", req.toCurrency)
                                            .queryParam("amount", req.amount);
                                }
                        ).retrieve()
                         .bodyToMono(JsonNode.class)
                         .map(json -> {
                             logger.info("API Response: {}", json.toString());
                             double result = json.get("convertedAmount").asDouble();
                             return new ErrorMessage(200, String.valueOf(result));
                         });
                    } catch (WebClientResponseException e) {
                        String errorMessage = null;
                        int statusValue = e.getStatusCode().value();
                        System.err.println("response: " + e.getResponseBodyAsString());
                        if (statusValue == 400) {
                            errorMessage = "無效金額/URL無效";
                            logger.error("400 Error: {}", errorMessage);
                            return Mono.just(new ErrorMessage(statusValue, errorMessage));
                        }
                        if (statusValue == 404) {
                            errorMessage = "找不到必須的URL";
                            logger.error("404 Error: {}", errorMessage);
                            return Mono.just(new ErrorMessage(statusValue, errorMessage));
                        }
                        if (statusValue == 401) {
                            errorMessage = "API KEY 無效/非活躍";
                            logger.error("401 Error: {}", errorMessage);
                            return Mono.just(new ErrorMessage(statusValue, errorMessage));
                        }
                        errorMessage = "發生未預期的錯誤，請聯繫管理員";
                        logger.error("Unexpected Error (status={}): {}", statusValue, errorMessage);
                        return Mono.just(new ErrorMessage(statusValue, errorMessage));
                    } catch (Exception e) {
                        String errorMessage = "系統異常: " + e.getMessage();
                        logger.error("System Exception: {}", errorMessage, e);
                        return Mono.just(new ErrorMessage(500, errorMessage));
                    }
                }).onErrorResume(e -> {
                    String errorMessage = "API 請求失敗: " + e.getMessage();
                    logger.error("API Error: {}", errorMessage);
                    return Mono.just(new ErrorMessage(500, errorMessage));
                });
    }
}

    // 定義天氣查詢的請求參數物件
    // private static class WeatherRequest {
    //     String city;
    //     String date;

    //     WeatherRequest(String city, String date) {
    //         this.city = city;
    //         this.date = date;
    //     }
    // }
    // public Mono<ErrorMessage> getHistoricalWeather(String city, String date) {
    //     try {
    //         WeatherRequest request = new WeatherRequest(city, date);
    //         return webClientUtil.buildRequest(
    //                 "/weather/historical", // String url
    //                 request,               // Object request
    //                 uriBuilder -> {        // 配置查詢參數
    //                     WeatherRequest req = (WeatherRequest) request;
    //                     uriBuilder
    //                             .queryParam("city", req.city)
    //                             .queryParam("date", req.date);
    //                 }
    //         ).retrieve()
    //          .bodyToMono(JsonNode.class)
    //          .map(json -> {
    //              logger.info("Weather API Response: {}", json.toString());
    //              double temperature = json.get("temperature").asDouble();
    //              String condition = json.get("condition").asText();
    //              String result = String.format("Temperature: %.1f°C, Condition: %s", temperature, condition);
    //              return new ErrorMessage(200, result);
    //          });
    //     } catch (WebClientResponseException e) {
    //         String errorMessage = null;
    //         int statusValue = e.getStatusCode().value();
    //         System.err.println("response: " + e.getResponseBodyAsString());
    //         if (statusValue == 400) {
    //             errorMessage = "無效的城市或日期格式";
    //             logger.error("400 Error: {}", errorMessage);
    //             return Mono.just(new ErrorMessage(statusValue, errorMessage));
    //         }
    //         if (statusValue == 404) {
    //             errorMessage = "找不到天氣資料";
    //             logger.error("404 Error: {}", errorMessage);
    //             return Mono.just(new ErrorMessage(statusValue, errorMessage));
    //         }
    //         if (statusValue == 401) {
    //             errorMessage = "API KEY 無效/非活躍";
    //             logger.error("401 Error: {}", errorMessage);
    //             return Mono.just(new ErrorMessage(statusValue, errorMessage));
    //         }
    //         errorMessage = "發生未預期的錯誤，請聯繫管理員";
    //         logger.error("Unexpected Error (status={}): {}", statusValue, errorMessage);
    //         return Mono.just(new ErrorMessage(statusValue, errorMessage));
    //     } catch (Exception e) {
    //         String errorMessage = "系統異常: " + e.getMessage();
    //         logger.error("System Exception: {}", errorMessage, e);
    //         return Mono.just(new ErrorMessage(500, errorMessage));
    //     }
    // }

// package com.example.moneychangeapi.service;

// import java.time.Duration;
// import java.util.ArrayList;
// import java.util.List;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory; // 導入 SLF4J 的日誌介面，用於記錄日誌。
// import org.springframework.stereotype.Service; // 導入日誌工廠，用於創建 Logger 實例。
// import org.springframework.web.reactive.function.client.WebClient;
// import org.springframework.web.reactive.function.client.WebClientResponseException;

// import com.example.moneychangeapi.config.MoneyChangeapiProperties;
// import com.example.moneychangeapi.model.ErrorMessage;
// import com.fasterxml.jackson.databind.JsonNode;

// import reactor.core.publisher.Mono;

// @Service
// public class ExchangeRateService {

//     private static final Logger logger = LoggerFactory.getLogger(ExchangeRateService.class);
//     // 記錄錯誤時使用
//     // Web用於呼叫API
//     private final WebClient webClient;
//     private final MoneyChangeapiProperties properties;

//     // Money...提供apikey配置
//     public ExchangeRateService(WebClient webClient, MoneyChangeapiProperties properties) {
//         this.webClient = webClient;
//         this.properties = properties;
//     }

//     private boolean isAllLetters(String word) {
//         for (int i = 0; i < word.length(); i++) {
//             char ch = word.charAt(i);
//             logger.debug("Checking char: {} (isLetter: {})", ch, Character.isLetter(ch));

//             // 檢查是否為中文
//             if (Character.UnicodeBlock.of(ch) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) {
//                 return false; // 包含中文，返回 false
//             }

//             // 檢查是否為英文字母
//             if (!((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z'))) {
//                 return false; // 非英文字母，返回 false
//             }
//         }
//         return true; // 全為英文字母，返回 true
//     }

    
//     // 透過建構子注入@Autowired
//     // 與 WebClientConfig 和 MoneyChangeapiProperties 的 @Bean 和 @Component關聯
//     // 獲取支援的貨幣符號列表
//     private Mono<List<String>> getSupportedCurrencies() {
//         //進行封裝
//         // return webClient.get()
//         //         .uri("https://api.currencyfreaks.com/v2.0/currency-symbols")
//         //         .retrieve()
//         //         .bodyToMono(JsonNode.class)
//         return webClient.get()
//                 .uri("https://api.currencyfreaks.com/v2.0/currency-symbols")
//                 .retrieve()
//                 .bodyToMono(JsonNode.class)
//                 .timeout(Duration.ofMillis(100))
//         //private Mono<WebClientUtil> getWebClientUtil(String url) {
//                 //return webClientUtil.getRequest(url)
//                 //.bodyToMono(JsonNode.class)
//                 //.retrieve()
//                 .map(json -> {
//                     JsonNode currencySymbols = json.get("currencySymbols");
//                     List<String> supportedCurrencies = new ArrayList<>();
//                     currencySymbols.fieldNames().forEachRemaining(supportedCurrencies::add);
//                     return supportedCurrencies;
//                 })
//                 .onErrorResume(e -> {
//                     logger.error("not support: {}", e.getMessage());
//                     // return Mono.just(new ArrayList<>()); // 若失敗，返回空列表
//                     return Mono.error(new RuntimeException("API 請求失敗: " + e.getMessage()));
//                 });
//     }

//     public Mono<ErrorMessage> getExchangeRate(String fromCurrency, String toCurrency, double amount) {
//         // public Mono<Double> getExchangeRate(String fromCurrency, String toCurrency,
//         // double amount) {
//         // 檢查是否都為英文字母
//         if (!isAllLetters(fromCurrency)) {
//             String errorMessage = String.format("\"%s\" FROM貨幣代碼必須為英文字母", fromCurrency);
//             logger.error("輸入字元中有非英文: from={}", fromCurrency);
//             return Mono.just(new ErrorMessage(400, errorMessage));
//         }

//         // 2. 僅 TO 非英文
//         if (!isAllLetters(toCurrency)) {
//             String errorMessage = String.format("\"%s\" TO貨幣代碼必須為英文字母", toCurrency);
//             logger.error("輸入字元中有非英文: to={}", toCurrency);
//             return Mono.just(new ErrorMessage(400, errorMessage));
//         }

//         // 3. FROM 和 TO 均非英文
//         if (!isAllLetters(fromCurrency) && !isAllLetters(toCurrency)) {
//             String errorMessage = String.format("\"%s\" \"%s\" FT貨幣代碼必須為英文字母", fromCurrency, toCurrency);
//             logger.error("輸入字元中有非英文: from={}, to={}", fromCurrency, toCurrency);
//             return Mono.just(new ErrorMessage(400, errorMessage));
//         }
//         // ERRORMESSAGE = "FROM" "TO"

//         // 檢查輸入貨幣符號是否均存在
//         // 訪問https://api.currencyfreaks.com/v2.0/currency-symbols
//         return getSupportedCurrencies()
//                 .flatMap(supportedCurrencies -> {
//                     boolean fromExists = supportedCurrencies.contains(fromCurrency.toUpperCase());
//                     boolean toExists = supportedCurrencies.contains(toCurrency.toUpperCase());
//                     String supportedCurrenciesStr = String.join("\", \"", supportedCurrencies);

//                     if (!fromExists && toExists) {
//                         String errorMessage = String.format("\"%s\" 貨幣符號不存在，請確認貨幣再重新嘗試\n支援的貨幣符號: \"%s\"",
//                                 fromCurrency, supportedCurrenciesStr);
//                         logger.error("貨幣不存在: from={}", fromCurrency);
//                         return Mono.just(new ErrorMessage(400, errorMessage));
//                     }
//                     if (fromExists && !toExists) {
//                         String errorMessage = String.format("\"%s\" 貨幣符號不存在，請確認貨幣再重新嘗試\n支援的貨幣符號: \"%s\"",
//                                 toCurrency, supportedCurrenciesStr);
//                         logger.error("貨幣不存在: to={}", toCurrency);
//                         return Mono.just(new ErrorMessage(400, errorMessage));
//                     }
//                     if (!fromExists && !toExists) {
//                         String errorMessage = String.format("\"%s\" \"%s\" 貨幣符號均不存在，請確認貨幣再重新嘗試\n支援的貨幣符號: \"%s\"",
//                                 fromCurrency, toCurrency, supportedCurrenciesStr);
//                         logger.error("貨幣不存在: from={}, to={}", fromCurrency, toCurrency);
//                         return Mono.just(new ErrorMessage(400, errorMessage));
//                     }
                    
//                     try {
//                         //可以封裝
//                         //private Mono<WebClientUtil> getWebClientUtil(String url) {
//                 //return webClientUtil.getRequest(url)
//                 //.bodyToMono(JsonNode.class)
//                 //.retrieve()
//                         return webClient.get()
//                                 .uri(uriBuilder -> {
//                                     // .url() Lambda表達式，queryParam 相關
//                                     java.net.URI uri = uriBuilder
//                                             // 提供最終api的url
//                                             .path("/convert/latest")
//                                             // 與baseUrl組合
//                                             // 添加鍵值 URL ?以後 key=value 格式 ，分隔以&為使用
//                                             // FORM=USD&TO=EUR&AMOUNT=1
//                                             // 來源貨幣=USD，目標貨幣=EUR，金額=1(1USD > ? EUR)
//                                             .queryParam("apikey", properties.getApiKey())
//                                             .queryParam("from", fromCurrency)
//                                             .queryParam("to", toCurrency)
//                                             .queryParam("amount", amount)
//                                             .build();
//                                     logger.info("API Request URL: {}", uri.toString());
//                                     return uri; // 將url返回給.uri()
//                                 })
//                                 .retrieve() // 觸發API呼叫
//                                 .bodyToMono(JsonNode.class) // 原有邏輯不變
//                                 .map(json -> { // 原有邏輯不變
//                                     logger.info("API Response: {}", json.toString());
//                                     double result = json.get("convertedAmount").asDouble();
//                                     return new ErrorMessage(200, String.valueOf(result));
//                                 });
//                     } catch (WebClientResponseException e) {
//                         String errorMessage = null;
//                         int statusValue = e.getStatusCode().value();
//                         System.err.println("response: " + e.getResponseBodyAsString());

//                         if (statusValue == 400) {
//                             errorMessage = "無效金額/URL無效";
//                             logger.error("400 Error: {}", errorMessage);
//                             return Mono.just(new ErrorMessage(statusValue, errorMessage));
//                         }
//                         if (statusValue == 404) {
//                             errorMessage = "找不到必須的URL";
//                             logger.error("404 Error: {}", errorMessage);
//                             return Mono.just(new ErrorMessage(statusValue, errorMessage));
//                         }
//                         if (statusValue == 401) {
//                             errorMessage = "API KEY 無效/非活躍";
//                             logger.error("401 Error: {}", errorMessage);
//                             return Mono.just(new ErrorMessage(statusValue, errorMessage));
//                         }
//                         errorMessage = "發生未預期的錯誤，請聯繫管理員";
//                         logger.error("Unexpected Error (status={}): {}", statusValue, errorMessage);
//                         return Mono.just(new ErrorMessage(statusValue, errorMessage));
//                     } catch (Exception e) {
//                         String errorMessage = "系統異常: " + e.getMessage();
//                         logger.error("System Exception: {}", errorMessage, e);
//                         return Mono.just(new ErrorMessage(500, errorMessage));
//                     }
//                 }).onErrorResume(e -> {
//                     String errorMessage = "API 請求失敗: " + e.getMessage();
//                     logger.error("API Error: {}", errorMessage);
//                     return Mono.just(new ErrorMessage(500, errorMessage));
//                 }); // flatMap 的閉合括號
//     } // 方法的閉合括號
// }