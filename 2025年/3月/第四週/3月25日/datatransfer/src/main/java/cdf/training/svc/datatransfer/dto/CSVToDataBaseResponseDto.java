package cdf.training.svc.datatransfer.dto;

import lombok.Data;

@Data
public class   CSVToDataBaseResponseDto {
    private String message;
    private String triggerTime;
    
    public CSVToDataBaseResponseDto(String message) {
        this.message = message;
    }
}