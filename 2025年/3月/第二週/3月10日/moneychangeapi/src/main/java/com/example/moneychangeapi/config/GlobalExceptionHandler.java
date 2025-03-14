// package com.example.moneychangeapi.config;

// import org.springframework.http.HttpStatus;
// import org.springframework.http.HttpStatusCode;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.ExceptionHandler;
// //import org.springframework.web.bind.annotation.RestControllerAdvice;
// import org.springframework.web.reactive.function.client.WebClientResponseException;

// import com.example.moneychangeapi.model.ErrorMessage;

// import reactor.core.publisher.Mono;

// @RestControllerAdvice
// public class GlobalExceptionHandler {

//     @ExceptionHandler(WebClientResponseException.class)
//     public Mono<ResponseEntity<ErrorMessage>> handleWebClientException(WebClientResponseException ex) {
//         HttpStatusCode statusCode = ex.getStatusCode();
//         HttpStatus status = HttpStatus.valueOf(statusCode.value()); // ✅ 轉回 HttpStatus

//         String errorMessage;
//         System.err.println("response: " + ex.getResponseBodyAsString());

//         if (status.is4xxClientError()) {
//             if (status.value() == 400) {
//                 System.out.println();
//                 errorMessage = "無效金額/URL無效";
//             } else if (status.value() == 404) {
//                 System.out.println("404錯誤訊息");
//                 errorMessage = "找不到必須的URL";
//             } else if (status.value() == 401) {
//                 errorMessage = "API KEY 無效/非活躍";
//             } else if (status.value() == 500) {
//                 errorMessage = "伺服器內部錯誤，請聯繫管理員";
//             } else {
//                 errorMessage = "客戶端錯誤: " + status.value();
//             }
//         } else if (status.is5xxServerError()) {
//             errorMessage = "伺服器錯誤，請稍後再試";
//         } else {
//             errorMessage = "發生未預期的錯誤，請聯繫管理員";
//         }
//         System.out.println("錯誤訊息st: " + status);
//         System.out.println("錯誤訊息stv: " + status.value());
//         ErrorMessage error = new ErrorMessage(status.value(), errorMessage);
        
//         System.out.println("錯誤訊息: " + error);
//         return Mono.just(ResponseEntity.status(status).body(error));
//     }
// }

