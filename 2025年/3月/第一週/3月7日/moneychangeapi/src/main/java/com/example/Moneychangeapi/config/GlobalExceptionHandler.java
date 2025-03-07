package com.example.Moneychangeapi.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.example.Moneychangeapi.model.ErrorMessage;

import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WebClientResponseException.class)
    public Mono<ResponseEntity<ErrorMessage>> handleWebClientException(WebClientResponseException ex) {
        HttpStatusCode statusCode = ex.getStatusCode();
        HttpStatus status = HttpStatus.valueOf(statusCode.value()); // ✅ 轉回 HttpStatus

        String errorMessage;

        if (status.is4xxClientError()) {
            if (status.value() == 400) {
                errorMessage = "請輸入正確的貨幣代碼";
            } else if (status.value() == 404) {
                errorMessage = "找不到該貨幣匯率資料，請確認貨幣代碼";
            } else if (status.value() == 401) {
                errorMessage = "API 金鑰無效，請檢查配置";
            } else {
                errorMessage = "客戶端錯誤: " + status.value();
            }
        } else if (status.is5xxServerError()) {
            errorMessage = "伺服器錯誤，請稍後再試";
        } else {
            errorMessage = "發生未預期的錯誤，請聯繫管理員";
        }

        ErrorMessage error = new ErrorMessage(status.value(), errorMessage);
        return Mono.just(ResponseEntity.status(status).body(error));
    }
}

