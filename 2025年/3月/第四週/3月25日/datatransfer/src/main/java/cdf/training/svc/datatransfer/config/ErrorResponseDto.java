package cdf.training.svc.datatransfer.config;

import cdf.training.svc.datatransfer.dto.BaseResponse;
import lombok.Data;

@Data
public class ErrorResponseDto {
    private String code;        // 錯誤碼，與 BaseResponse 的 code 一致
    private String message;     // 錯誤訊息，與 BaseResponse 的 message 一致
    private String triggerTime; // 觸發時間，與 BaseResponse 的 triggerTime 一致

    public ErrorResponseDto(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ErrorResponseDto(BaseResponse.ResponseCode errorCode, String message) {
        this.code = errorCode.getCode();
        this.message = message != null ? message : errorCode.getDefaultMessage();
    }

    public ErrorResponseDto(String code, String message, String triggerTime) {
        this.code = code;
        this.message = message;
        this.triggerTime = triggerTime;
    }
}