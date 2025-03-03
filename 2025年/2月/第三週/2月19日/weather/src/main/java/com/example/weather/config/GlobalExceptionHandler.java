package com.example.weather.config;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.example.weather.model.ErrorMessage;

import reactor.core.publisher.Mono;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WebClientResponseException.class)
    public Mono<ResponseEntity<ErrorMessage>> handleWebClientResponseException(WebClientResponseException ex) {
        HttpStatusCode status = ex.getStatusCode(); // 改為 HttpStatusCode
        String errorMessage;

        // 使用 is4xxClientError() 等方法判斷狀態碼範圍
        if (status.is4xxClientError()) {
            if (status.value() == 400) {
                errorMessage = "城市名稱輸入錯誤，請輸入正確城市名";
            } else if (status.value() == 404) {
                errorMessage = "找不到該城市的天氣資料，請確認城市名稱";
            } else if (status.value() == 401) {
                errorMessage = "API 金鑰無效，請檢查配置";
            } else {
                errorMessage = "客戶端錯誤: " + status.value();
            }
        } else if (status.is5xxServerError()) {
            errorMessage = "伺服器錯誤，請稍後再試";
        } else {
            errorMessage = "發生未預期的錯誤，請聯繫管理員"; // + ex.getMessage(); = 不公開錯誤資訊
        }

        ErrorMessage error = new ErrorMessage(status.value(), errorMessage);
        return Mono.just(ResponseEntity.status(status).body(error));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorMessage>> handleGenericException(Exception ex) {
        ErrorMessage error = new ErrorMessage(500, "伺服器內部錯誤，請聯繫管理員");
        return Mono.just(ResponseEntity.status(HttpStatusCode.valueOf(500)).body(error));
    }
}