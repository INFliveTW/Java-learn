package cdf.training.svc.datatransfer.dto;

import lombok.Data;

@Data
public class ErrorResponseDto {
    private String code;
    private String message;
    private String triggerTime;

    public ErrorResponseDto(String code, String message, String triggerTime) {
        this.code = code;
        this.message = message;
        this.triggerTime = triggerTime;
    }

    @Override
    public String toString() {
        return "ErrorResponseDto(code=" + code + ", message=" + message + ", triggerTime=" + triggerTime + ")";
    }
}