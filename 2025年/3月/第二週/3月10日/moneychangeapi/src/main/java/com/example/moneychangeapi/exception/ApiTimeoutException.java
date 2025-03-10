// package com.example.moneychangeapi.exception;
//  //超時例外
// public class ApiTimeoutException extends RuntimeException {
//     public ApiTimeoutException(String message) {
//         super(message);
//     }
// }

package com.example.moneychangeapi.exception;

public class ApiTimeoutException extends RuntimeException {
    public ApiTimeoutException(String message) {
        super(message);
    }

    // 新增帶狀態碼的構造函數，以便與 ErrorMessage 整合
    public ApiTimeoutException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    private int statusCode = 408; // 預設為 408 Request Timeout

    public int getStatusCode() {
        return statusCode;
    }
}