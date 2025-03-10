package com.example.Moneychangeapi.config;

import org.springframework.http.HttpStatus;
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
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String errorMessage;

        if (status.is4xxClientError()) {
            errorMessage = switch (status.value()) {
                case 400 -> "請輸入正確的貨幣代碼";
                case 404 -> "找不到該貨幣匯率資料，請確認貨幣代碼";
                case 401 -> "API 金鑰無效，請檢查配置";
                default -> "客戶端錯誤: " + status.value();
            };
        } else if (status.is5xxServerError()) {
            errorMessage = "伺服器錯誤，請稍後再試";
        } else {
            errorMessage = "發生未預期的錯誤，請聯繫管理員";
        }

        ErrorMessage error = new ErrorMessage(status.value(), errorMessage);
        return Mono.just(ResponseEntity.status(status).body(error));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ErrorMessage>> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorMessage error = new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error));
    }
}