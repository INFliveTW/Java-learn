package cdf.training.svc.datatransfer.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(example = "{\"COMPANY\": \"string\", \"EXCUTETIME\": \"string\"}") // 設置 Swagger 預設範例為空物件
public class CSVToDataBaseRequestDto {
    private String COMPANY;
    private String EXCUTETIME;
    //private String triggerTime;
}

// package cdf.training.svc.datatransfer.dto;

// import io.swagger.v3.oas.annotations.media.Schema;
// import lombok.Data;

// @Data
// @Schema(example = "{\"COMPANY\": \"string\", \"EXCUTETIME\": \"string\"}")
// public class CSVToDataBaseRequestDto {
//     @Schema(description = "Company name", example = "string")
//     private String COMPANY;

//     @Schema(description = "Execution time", example = "string")
//     private String EXCUTETIME;
// }