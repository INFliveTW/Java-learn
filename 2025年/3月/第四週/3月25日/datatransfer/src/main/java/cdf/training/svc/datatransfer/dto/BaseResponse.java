package cdf.training.svc.datatransfer.dto;

import lombok.Data;

@Data
public class BaseResponse<T> {
    private String code;        // 回應碼（成功或錯誤碼）
    private String message;     // 回應訊息
    private T data;             // 回應數據（成功時使用，泛型支持不同類型）
    private String triggerTime; // 觸發時間

    // 成功回應的構造函數
    public BaseResponse(String message, T data, String triggerTime) {
        this.code = ResponseCode.SUCCESS.getCode();
        this.message = message;
        this.data = data;
        this.triggerTime = triggerTime;
    }

    // 錯誤回應的構造函數
    public BaseResponse(ResponseCode errorCode, String message, String triggerTime) {
        this.code = errorCode.getCode();
        this.message = message;
        this.data = null;
        this.triggerTime = triggerTime;
    }

    // 定義回應碼的枚舉
    public enum ResponseCode {
        SUCCESS("200", "成功"),
        SFTP_PERMISSION_DENIED("403", "SFTP 伺服器拒絕訪問，請檢查權限"),
        SFTP_FILE_NOT_FOUND("404", "SFTP 資料夾沒有CSV檔案，請確認SFTP"),
        CSV_PARSE_ERROR("400", "CSV 檔案解析失敗，請確認檔案格式正確"),
        CSV_EMPTY_ERROR("204", "CSV 檔案內容沒有任何資料，請確認文件內容"),
        UNKNOWN_ERROR("500", "發生未知錯誤"),
        SFTP_CONNECTION_ERROR("503", "無法連接到 SFTP 伺服器，請檢查配置或網路狀態");

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