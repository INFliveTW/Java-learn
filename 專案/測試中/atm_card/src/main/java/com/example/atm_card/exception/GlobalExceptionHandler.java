package com.example.atm_card.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice  // 讓此類別成為全局異常處理器
public class GlobalExceptionHandler {

    /**
     * 處理找不到資料的異常
     * @param ex 自訂的 ResourceNotFoundException
     * @param request 請求資訊
     * @return 錯誤回應 JSON
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());  // 紀錄錯誤發生時間
        body.put("status", HttpStatus.NOT_FOUND.value());  // HTTP 狀態碼
        body.put("error", "找不到資源");  // 錯誤類型
        body.put("message", ex.getMessage());  // 具體錯誤訊息
        body.put("path", request.getDescription(false));  // 錯誤發生的 API 路徑

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * 處理參數驗證失敗的異常（例如 @Valid 驗證失敗）
     * @param ex MethodArgumentNotValidException
     * @return 錯誤回應 JSON
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "參數驗證失敗");

        // 取得所有錯誤訊息
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        body.put("message", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * 處理所有未預期的異常（全局異常處理）
     * @param ex 例外物件
     * @param request 請求資訊
     * @return 錯誤回應 JSON
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(
            Exception ex, WebRequest request) {
        
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "伺服器內部錯誤");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
