package cdf.training.svc.datatransfer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Response物件")
public class BaseResponse {
    @Schema(description = "回覆狀態物件")
    private Metadata metadata;
    
    @Schema(description = "回傳資料")
    private Object data;

    // 成功的建構子
    public BaseResponse(Object data) {
        this.metadata = new Metadata(true, null, null);
        this.data = data;
    }

    // 業務錯誤的建構子
    public BaseResponse(String errorCode, String errorDesc) {
        this.metadata = new Metadata(false, errorCode, errorDesc);
        this.data = null;
    }

    @Data
    @Schema(description = "回覆狀態物件")
    public static class Metadata {
        @Schema(description = "回覆狀態")
        private Boolean status;
        
        @Schema(description = "錯誤代碼")
        private String errorCode;
        
        @Schema(description = "錯誤說明")
        private String errorDesc;

        public Metadata(Boolean status, String errorCode, String errorDesc) {
            this.status = status;
            this.errorCode = errorCode;
            this.errorDesc = errorDesc;
        }
    }

    // 內部 enum ResponseCode
    public enum ResponseCode {
        SUCCESS("SUCCESS", "資料處理成功"),
        SFTP_PERMISSION_DENIED("SFTP_001", "SFTP 伺服器拒絕訪問，請檢查權限"),
        SFTP_FILE_NOT_FOUND("SFTP_002", "SFTP 資料夾沒有CSV檔案，請確認SFTP"),
        CSV_PARSE_ERROR("CSV_001", "CSV 檔案解析失敗，請確認檔案格式正確"),
        CSV_EMPTY_ERROR("CSV_002", "CSV 檔案內容沒有任何資料，請確認文件內容"),
        CSV_MISSING_FIELDS("CSV_003", "CSV檔案，欄位缺少，請確認檔案"),
        CSV_MISSING_DATA("CSV_004", "CSV檔案，資料缺少，請確認檔案"),
        SFTP_CONNECTION_ERROR("SFTP_003", "無法連接到 SFTP 伺服器，請檢查配置或網路狀態"),
        SQL_CONNECTION_ERROR("SQL_001", "無法連線到資料庫，請檢查配置或網路狀態"),
        SQL_WRITE_ERROR("SQL_002", "資料庫寫入失敗，請檢查資料庫連線或權限"),
        UNKNOWN_ERROR("UNKNOWN_001", "發生未知錯誤");

        private final String code;
        private final String defaultMessage;

        ResponseCode(String code, String defaultMessage) {
            this.code = code;
            this.defaultMessage = defaultMessage;
        }

        public String getCode() {
            return code;
        }

        public String getDefaultMessage() {
            return defaultMessage;
        }
    }
}