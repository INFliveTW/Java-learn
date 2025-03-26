package cdf.training.svc.datatransfer.dto;
import lombok.Data;

@Data
public class ErrorResponseDto {
    private String errorCode;
    private String message;
    private String triggerTime;

    public ErrorResponseDto(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
