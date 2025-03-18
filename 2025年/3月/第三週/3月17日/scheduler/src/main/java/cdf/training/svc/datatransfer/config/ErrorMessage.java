package cdf.training.svc.datatransfer.config;

import lombok.Data;

@Data
public class ErrorMessage {
    private int status;
    private String message;
    public ErrorMessage(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
