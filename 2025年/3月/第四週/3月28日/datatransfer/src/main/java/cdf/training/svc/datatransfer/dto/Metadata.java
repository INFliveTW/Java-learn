package cdf.training.svc.datatransfer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "回覆狀態物件")
public class Metadata {

    @Schema(description = "回覆狀態")
    private Boolean status;

    @Schema(description = "錯誤代碼")
    private String errorCode;

    @Schema(description = "錯誤說明")
    private String errorDesc;

    // 成功時的構造函數
    public Metadata() {
        this.status = true;
        this.errorCode = null;
        this.errorDesc = null;
    }

    // 錯誤時的構造函數
    public Metadata(String errorCode, String errorDesc) {
        this.status = false;
        this.errorCode = errorCode;
        this.errorDesc = errorDesc;
    }
}