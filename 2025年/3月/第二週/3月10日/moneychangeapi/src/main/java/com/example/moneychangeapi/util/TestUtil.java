package com.example.moneychangeapi.util;

public class TestUtil {
    
}



//return TestUtil.get()
//.uri("")


// private Mono<List<String>> getSupportedCurrencies() {
//     return webClient.get()
//             .uri("https://api.currencyfreaks.com/v2.0/currency-symbols")
//             .retrieve()
//             .bodyToMono(JsonNode.class)

// CurrencyRequest request = new CurrencyRequest(fromCurrency, toCurrency, amount);
//                         return webClientUtil.buildRequest(
//                                 "https://api.currencyfreaks.com/v2.0/convert/latest", // String url
//                                 request,           // Object request
//                                 uriBuilder -> {    // 配置查詢參數
//                                     CurrencyRequest req = (CurrencyRequest) request;
//                                     uriBuilder
//                                             .queryParam("from", req.fromCurrency)
//                                             .queryParam("to", req.toCurrency)
//                                             .queryParam("amount", req.amount);
//                                 }
//                         ).retrieve()
//                          .bodyToMono(JsonNode.class)

// 定義貨幣轉換的請求參數物件
// private static class CurrencyRequest {
//     String fromCurrency;
//     String toCurrency;
//     double amount;

//     CurrencyRequest(String fromCurrency, String toCurrency, double amount) {
//         this.fromCurrency = fromCurrency;
//         this.toCurrency = toCurrency;
//         this.amount = amount;
//     }
// }

//