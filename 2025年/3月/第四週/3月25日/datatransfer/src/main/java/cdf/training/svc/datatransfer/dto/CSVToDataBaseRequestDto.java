package cdf.training.svc.datatransfer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(example = "{}") // 設置 Swagger 預設範例為空物件
public class CSVToDataBaseRequestDto {
    private String COMPANY;
    private String EXCUTETIME;
    private String triggerTime;
}