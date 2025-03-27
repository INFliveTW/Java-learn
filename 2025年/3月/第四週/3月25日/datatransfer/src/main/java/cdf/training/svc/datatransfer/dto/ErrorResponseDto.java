package cdf.training.svc.datatransfer.dto;
import cdf.training.svc.datatransfer.dto.BaseResponse.ResponseCode;
import lombok.Data;

@Data
public class ErrorResponseDto {
    private ResponseCode errorCode;
    private String message;
    private String triggerTime;

    public ErrorResponseDto(ResponseCode errorCode2, String message) {
        this.errorCode = errorCode2;
        this.message = message;
    }
}
