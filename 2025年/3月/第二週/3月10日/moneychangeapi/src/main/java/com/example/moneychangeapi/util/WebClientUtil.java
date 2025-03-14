package com.example.moneychangeapi.util;

import java.net.URI;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

public class WebClientUtil {
    private static final Logger logger = LoggerFactory.getLogger(WebClientUtil.class);
    private final WebClient webClient;
    private final String apiKey;

    public WebClientUtil(WebClient webClient, String apiKey) {
        this.webClient = webClient;
        this.apiKey = apiKey;
    }

    /**
     * 通用的 GET 請求構建方法
     * @param path API 路徑（由 Service 提供，例如 "/convert/latest" 或 "/weather/historical"）
     * @param requestParams 請求參數物件（由 Service 提供，包含具體參數）
     * @param paramsConfigurer 根據 requestParams 配置查詢參數的 Consumer
     * @return WebClient.RequestHeadersSpec<?>，待進一步處理的請求物件
     */

     public WebClient.RequestHeadersSpec<?> buildRequest(
        String path,
        Object requestParams,
        Consumer<UriBuilder> paramsConfigurer) {
    return webClient.get()
            .uri(uriBuilder -> {
                uriBuilder.path(path);              // 設置 API 路徑
                uriBuilder.queryParam("apikey", apiKey); // 通用參數：apiKey
                paramsConfigurer.accept(uriBuilder);     // 動態設置其他參數
                URI uri = uriBuilder.build();
                logger.info("API Request URL: {}", uri.toString());
                return uri;
                });
    }
}